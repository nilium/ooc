package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class ValuedReturn extends Return implements MustBeResolved {

	protected Expression expression;

	public ValuedReturn(Expression expression, Token startToken) {
		super(startToken);
		this.expression = expression;
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		expression.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(super.replace(oldie, kiddo)) return true;
		
		if(oldie == expression) {
			expression = (Expression) kiddo;
			return true;
		}
		
		return super.replace(oldie, kiddo);
		
	}

	public boolean isResolved() {
		return false;
	}

	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		int funcIndex = stack.find(FunctionDecl.class);
		if(funcIndex == -1) {
			throw new OocCompilationError(this, stack, "'return' outside a function: wtf?");
		}
		FunctionDecl decl = (FunctionDecl) stack.get(funcIndex);
		Type returnType = decl.getReturnType();
		TypeParam param = getTypeParam(stack, returnType.getName());
		if(param != null) {
			unwrapToMemcpy(stack, decl, param);
			//return Response.RESTART;
			return Response.LOOP;
		}
		
		if(returnType.isSuperOf(expression.getType())) {
			expression = new Cast(expression, returnType, expression.startToken);
		}
		
		return Response.OK;
		
	}

	@SuppressWarnings("unchecked")
	private void unwrapToMemcpy(NodeList<Node> stack, FunctionDecl decl,
			Declaration genericType) {
		
		if(!(expression.canBeReferenced())) {
			VariableDeclFromExpr vdfe = new VariableDeclFromExpr(generateTempName("retval", stack), expression, startToken);
			vdfe.setType(expression.getType());
			expression = vdfe;
			stack.push(this);
			vdfe.unwrapToVarAcc(stack);
			stack.pop(this);
			return;
		}
		
		FunctionCall call = new FunctionCall("memcpy", startToken);
		VariableAccess returnArgAcc = new VariableAccess(decl.getReturnArg(), startToken);
		NodeList<Expression> args = call.getArguments();
		args.add(returnArgAcc);
		
		VariableAccess tAccess = new VariableAccess(genericType.getName(), startToken);
		MemberAccess sizeAccess = new MemberAccess(tAccess, "size", startToken);
		
		if(expression instanceof ArrayAccess) {
			ArrayAccess arrAccess = (ArrayAccess) expression;
			assert(arrAccess.indices.size() == 1);
			expression = new Add(new AddressOf(arrAccess.variable, startToken),
					new Mul(arrAccess.indices.getFirst(), sizeAccess, startToken), startToken);
		} else {
			expression = new AddressOf(expression, startToken);
		}
		
		args.add(expression);
		args.add(sizeAccess);
		
		If if1 = new If(returnArgAcc, startToken);
		if1.getBody().add(new Line(call));
		stack.peek().replace(this, if1);
		
		int lineIndex = stack.find(Line.class);
		((NodeList<Node>) stack.get(lineIndex - 1)).addAfter(stack.get(lineIndex), new Line(new Return(startToken)));
		
	}
	
	@Override
	public String toString() {
		return "return "+expression;
	}
	
}
