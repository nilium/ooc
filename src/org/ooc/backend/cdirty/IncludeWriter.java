package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.Include.Define;
import org.ooc.frontend.model.Include.Mode;

public class IncludeWriter {

	public static void write(Include include, CGenerator cgen) throws IOException {
		
		if(include.getVersion() != null) {
			VersionBlockWriter.writeVersionBlockStart(include.getVersion(), cgen);
		}
		
		for(Define define: include.getDefines()) {
			cgen.current.nl().app("#ifndef ").app(define.name);
			cgen.current.nl().app("#define ").app(define.name);
			if(define.value != null) cgen.current.app(' ').app(define.value);
			cgen.current.nl().app("#endif");
		}
		if(include.getMode() == Mode.PATHY) {
			cgen.current.nl().app("#import <").app(include.getPath()).app(".h>");
		} else {
			cgen.current.nl().app("#import \"").app(include.getPath()).app(".h\"");
		}
		for(Define define: include.getDefines()) {
			cgen.current.nl().app("#undef ").app(define.name).app(' ');
		}
		
		if(include.getVersion() != null) {
			VersionBlockWriter.writeVersionBlockEnd(cgen);
		}
	}
	
}
