package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.tokens.Token;

public class Not extends UnaryOperation {

	public static Type type = new Type("Bool", Token.defaultToken);
	
	public Not(Expression inner, Token startToken) {
		super(inner, startToken);
	}

	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public OpType getOpType() {
		return OpType.NOT;
	}
	
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

}
