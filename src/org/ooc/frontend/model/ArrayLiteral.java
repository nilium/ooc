package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Iterator;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.interfaces.MustBeUnwrapped;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class ArrayLiteral extends Literal implements MustBeUnwrapped, MustBeResolved {

	private static Type defaultType = NullLiteral.type;
	protected Type innerType = null;
	protected Type type = defaultType;
	
	protected NodeList<Expression> elements;
	
	public ArrayLiteral(Token startToken) {
		super(startToken);
		elements = new NodeList<Expression>(startToken);
	}
	
	public int getDepth() {
		if(elements.isEmpty() || !(elements.getFirst() instanceof ArrayLiteral)) return 1;
		return 1 + ((ArrayLiteral) elements.getFirst()).getDepth();
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == type) {
			type = (Type) kiddo;
			return true;
		}
		
		return false;
	}

	public Type getType() {
		return type;
	}
	
	public Type getInnerType() {
		return innerType;
	}
	
	public NodeList<Expression> getElements() {
		return elements;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public void acceptChildren(Visitor visitor) throws IOException {
		type.accept(visitor);
		elements.accept(visitor);
	}

	public boolean hasChildren() {
		return true;
	}

	public boolean isResolved() {
		return type != defaultType;
	}
	
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		if(type != defaultType) return Response.OK;
		
		if(!elements.isEmpty()) {
			Iterator<Expression> iter = elements.iterator();
			Expression first = iter.next();
			Type innerType = first.getType();
			if(innerType == null) {
				if(fatal) {
					throw new OocCompilationError(first, stack, "Couldn't resolve type of "
							+first+" in an "+innerType+" array literal");
				}
				return Response.LOOP;
			}
			
			while(iter.hasNext()) {
				Expression element = iter.next();
				if(element.getType() == null) {
					if(fatal) {
						throw new OocCompilationError(element, stack, "Couldn't resolve type of "
								+element+" in an "+innerType+" array literal");
					}
					return Response.LOOP;
				}
				if(!element.getType().fitsIn(innerType)) {
					throw new OocCompilationError(element, stack, "Encountered a "
							+element.getType()+" in a "+innerType+"[] array literal.");
				}
			}

			this.innerType = innerType;
			this.type = new Type(innerType.name, innerType.pointerLevel + 1, startToken);
			type.getTypeParams().addAll(innerType.getTypeParams());
			type.setArray(true);
			stack.push(this);
			type.resolve(stack, res, fatal);
			innerType.resolve(stack, res, fatal);
			stack.pop(this);
		}
		
		if(type == defaultType && fatal) {
			throw new OocCompilationError(this, stack, "Couldn't figure out type of ArrayLiteral with elements "+elements);
		}
		return (type == defaultType) ? Response.LOOP : Response.OK; 
		
	}

	public boolean unwrap(NodeList<Node> stack) throws IOException {
		
		if(stack.peek() instanceof Cast || stack.peek() instanceof Foreach) {
			//System.out.println("ArrayLiteral "+this+" in a "+stack.peek().getClass().getSimpleName()+". Not unwrapping =)");
			return false;
		}
		
		if(stack.peek(2) instanceof ArrayLiteral) {
			return false;
		}
		
		int varDeclIndex = stack.find(VariableDecl.class);
		
		// if stack.size() - varDeclIndex > 3, we're nested in another varDecl
		//, thus we need to unwrap
		if(varDeclIndex == -1 || (stack.size() - varDeclIndex) > 3) {
			stack.peek().replace(this, new VariableDeclFromExpr(
					generateTempName("array", stack), this, startToken));
			return true;
		}
		
		return false;
		
	}

}
