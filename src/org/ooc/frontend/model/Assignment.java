package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class Assignment extends BinaryOperation {

	protected boolean dead = false;
	
	public static enum Mode {
		REGULAR,
		ADD,
		SUB,
		DIV,
		MUL,
		B_XOR,
		B_OR,
		B_AND,
		B_LSHIFT,
		B_RSHIFT,
	}
	
	protected Mode mode;
	
	public Assignment(Expression left, Expression right, Token startToken) {
		this(Mode.REGULAR, left, right, startToken);
	}
	
	public Assignment(Mode mode, Expression lvalue, Expression rvalue, Token startToken) {
		super(lvalue, rvalue, startToken);
		this.mode = mode;
		this.left = lvalue;
		this.right = rvalue;
	}
	
	public Mode getMode() {
		return mode;
	}

	@Override
	public Type getType() {
		return left.getType();
	}
	
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		left.accept(visitor);
		right.accept(visitor);
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == left) {
			left = (Access) kiddo;
			return true;
		}
		if(oldie == right) {
			right = (Expression) kiddo;
			return true;
		}
		return false;
	}

	public String getSymbol() {
		switch(mode) {
			case ADD:
				return "+=";
			case DIV:
				return "/=";
			case MUL:
				return "*=";
			case SUB:
				return "-=";
			case B_AND:
				return "&=";
			case B_LSHIFT:
				return "<<=";
			case B_OR:
				return "|=";
			case B_RSHIFT:
				return ">>=";
			case B_XOR:
				return "^=";
			case REGULAR:
				return "=";
		}
		return "unknown";
	}

	@Override
	public OpType getOpType() {
		switch(mode) {
		case ADD:
			return OpType.ADD_ASS;
		case DIV:
			return OpType.DIV_ASS;
		case MUL:
			return OpType.MUL_ASS;
		case SUB:
			return OpType.SUB_ASS;
		case B_XOR:
			return OpType.B_XOR_ASS;
		case B_OR:
			return OpType.B_OR_ASS;
		case B_AND:
			return OpType.B_AND_ASS;
		case B_LSHIFT:
			return OpType.B_LSHIFT_ASS;
		case B_RSHIFT:
			return OpType.B_RSHIFT_ASS;
		case REGULAR:
			return OpType.ASS;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "Assignment: "+left.toString()+" = "+right.toString();
	}
	
	@Override
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {

		if(dead) return Response.OK;
		
		if(!(stack.peek() instanceof Line)) {
			throw new OocCompilationError(this, stack,
					"It's illegal to use an assignment as an expression (it's for your own good.) Did you mean '==' ?");
		}
		
		if(right.getType() != null && left.getType() != null) {
			if(left.getType().isSuperOf(right.getType())) {
				right = new Cast(right, left.getType(), right.startToken);
			}
		}
		
		if(left.getType() == null) {
			if(fatal) throw new OocCompilationError(left, stack, "Left type of assignment unresolved: "+left+" (btw, stack = "+stack.toString(true));
			return Response.LOOP;
		}
		if(right.getType() == null) {
			if(fatal) throw new OocCompilationError(right, stack, "Right type of assignment unresolved: "+right);
			return Response.LOOP;
		}
		
		boolean isGeneric = false;
		Expression realLeft = null;
		Expression realRight = null;
		Expression size = null;
		if(left.getType().isGeneric()) {
			isGeneric = true;
			TypeParam genericType = (TypeParam) left.getType().getRef();
			VariableAccess tAccess = new VariableAccess(genericType.getName(), startToken);
			size = new MemberAccess(tAccess, "size", startToken);
			realLeft = new AddressOf(left, left.startToken);
			realRight = new AddressOf(right, right.startToken);
		}
		
		if(!isGeneric && right.getType().isGeneric() && !(left instanceof ArrayAccess) && !isGeneric) {
			right = new Cast(right, left.getType(), right.startToken);
		}
		
		if(isGeneric) {
			if(left instanceof ArrayAccess) {
				ArrayAccess arrAcc = (ArrayAccess) left;
				Expression var = arrAcc.getVariable();
				if(var.getType().isGeneric()) {
					VariableAccess tAccess = new VariableAccess(var.getType().getRef().getName(), startToken);
					MemberAccess sizeAccess = new MemberAccess(tAccess, "size", startToken);
					realLeft = new Add(new AddressOf(var, var.startToken), mul(arrAcc, sizeAccess), startToken);
				}
			}
			if(right instanceof ArrayAccess) {
				ArrayAccess arrAcc = (ArrayAccess) right;
				Expression var = arrAcc.getVariable();
				if(var.getType().isGeneric()) {
					VariableAccess tAccess = new VariableAccess(var.getType().getRef().getName(), startToken);
					MemberAccess sizeAccess = new MemberAccess(tAccess, "size", startToken);
					realRight = new Add(new AddressOf(var, var.startToken), mul(arrAcc, sizeAccess), startToken);
				}
			} 
			
			if(!right.canBeReferenced()) {
				// shortcut casts, otherwise we never know the real type.
				Cast cast1 = null;
				while(right instanceof Cast) {
					if(cast1 == null) {
						cast1 = ((Cast) right);
					}
					right = ((Cast) right).getExpression();
				}
				VariableDeclFromExpr vdfe = new VariableDeclFromExpr(
						generateTempName("genref", stack), right, right.startToken);
				vdfe.setType(right.getType()); // fixate the type
				addBeforeLine(stack, vdfe);
				vdfe.unwrapToVarAcc(stack);
				right = new VariableAccess(vdfe, vdfe.startToken);
				realRight = new AddressOf(right, right.startToken);
				if(cast1 != null) {
					cast1.setExpression(realRight);
					realRight = cast1;
				}
			}
			
			if(realLeft != null && realRight != null && size != null
					&& (left.getType().isFlat() || left.getType().isArray())) {
				unwrapToMemcpy(stack, realLeft, realRight, size);
				return Response.LOOP;
			}
		}
		
		return super.resolve(stack, res, fatal);
		
	}

	private Mul mul(ArrayAccess arrAcc, MemberAccess sizeAccess) {
		assert(arrAcc.indices.size() == 1);
		return new Mul(arrAcc.indices.getFirst(), sizeAccess, startToken);
	}

	private void unwrapToMemcpy(NodeList<Node> stack, Expression realLeft, Expression realRight, Expression size) {
		
		if(realLeft == null || realRight == null || size == null) {
			throw new Error("Heh :/ either of those are null: realLeft = "+realLeft
					+", realRight = "+realRight+", size = "+size);
		}
		
		FunctionCall call = new FunctionCall("memcpy", startToken);
		NodeList<Expression> args = call.getArguments();
		args.add(realLeft);
		args.add(realRight);
		args.add(size);
		
		Node parent = stack.peek();

		// why test if left is an array?
		// because when declaring a variable of type which resolves to a TypeParam, e.g.
		// value: T
		// Then it translates to
		// uint8_t value[T->size]
		// That's an optimization. Thus, this will never be null, and it'll never
		// need being malloc'd.
		if(realLeft instanceof Access && !(left.getType().isArray())) {
		
			Block block = new Block(startToken);
			
			If if1 = new If(new Not(realLeft, realLeft.startToken), startToken);
			FunctionCall alloc = new FunctionCall("gc_malloc", startToken);
			alloc.getArguments().add(size);
			Assignment allocAss = new Assignment(realLeft,
					alloc, startToken);
			allocAss.dead = true;
			if1.getBody().add(new Line(allocAss));
			block.getBody().add(new Line(if1));
			block.getBody().add(new Line(call));
			
			// FIXME I'm not entirely sure this is right.
			if(parent instanceof NodeList<?>) {
				parent.replace(this, new Line(block));
			} else {
				parent.replace(this, block);
			}
		
		} else {
			
			parent.replace(this, call);
			
		}
		
	}

	@Override
	public int getPriority() {
		return 120;
	}
	
}
