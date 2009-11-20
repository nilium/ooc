package org.ooc.frontend.parser;

import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

/* Hacked copy of the ExternParser to handle selectors */
public class SelectorParser {

	public static String parse(SourceReader sReader, TokenReader reader) throws CompilationFailedError {
		
		String selectorName = null;
		
		if(reader.peek().type == TokenType.SELECTOR_KW) {
			reader.skip();
			if(reader.peek().type == TokenType.OPEN_PAREN) {
				reader.skip();
				Token nameToken = reader.read();
				selectorName = nameToken.get(sReader);
				
				if (reader.peek().type == TokenType.COLON) {
					reader.skip();
					
					selectorName += ":";
					
					while (reader.peek().type != TokenType.CLOS_PAREN) {
						nameToken = reader.read();
						selectorName += nameToken.get(sReader);
						
						if ( reader.read().type != TokenType.COLON ) {
							throw new CompilationFailedError(null,
								"Expecting colon in selector name, but got "+reader.peek());
						}
						
						selectorName += ":";
					}
				}
				
				if(reader.read().type != TokenType.CLOS_PAREN) {
					throw new CompilationFailedError(null,
							"Expected closing parenthesis after selector specification, but got "+reader.peek());
				}
			} else {
				selectorName = "";
			}
		}
		return selectorName;
	}
	
}
