package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class NSStringLiteral extends StringLiteral {
	public static Type type = new Type("NSString", Token.defaultToken);
	
	public NSStringLiteral(String value, Token startToken) {
		super(value, startToken);
	}
}
