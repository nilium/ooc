package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.HashMap;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.TypeParam;
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.walkers.Opportunist;
import org.ooc.middle.walkers.SketchyNosy;

/**
 * The Checker makes sure everything has been resolved properly. It also makes
 * sure type names are CamelCase and func/vars camelCase
 * 
 * @author Amos Wenger
 */
public class Checker implements Hobgoblin {

	final HashMap<String, FunctionDecl> funcNames = new HashMap<String, FunctionDecl>();
	final HashMap<TypeDecl, HashMap<String, FunctionDecl>> classFuncNames = new HashMap<TypeDecl, HashMap<String, FunctionDecl>>();
	
	public boolean process(Module module, BuildParams params) throws IOException {
		
		SketchyNosy.get(new Opportunist<Node>() {

			public boolean take(Node node, NodeList<Node> stack) throws IOException {
				if(node instanceof FunctionCall) checkFunctionCall((FunctionCall) node, stack);
				else if(node instanceof VariableAccess) checkVariableAccess((VariableAccess) node, stack);
				else if(node instanceof FunctionDecl) checkFunctionDecl((FunctionDecl) node, stack);
				else if(node instanceof VariableDecl) checkVariableDecl((VariableDecl) node, stack);
				else if(node instanceof TypeDecl) checkTypeDecl((TypeDecl) node, stack);
				else if(node instanceof ValuedReturn) checkValuedReturn((ValuedReturn) node, stack);
				return true;
			}
			
			private void checkFunctionCall(FunctionCall node, NodeList<Node> stack) {
				if(node.getImpl() == null) {
					throw new OocCompilationError(node, stack,
							node.getClass().getSimpleName()+" to "+node.getName()
							+" hasn't been resolved :/");
				}
			}
			
			private void checkVariableAccess(VariableAccess node, NodeList<Node> stack) {
				if(node.getRef() == null) {
					throw new OocCompilationError(node, stack,
							node.getClass().getSimpleName()+" to "+node.getName()
							+" hasn't been resolved :S stack = "+stack.toString(true));
				}
			}
			
			private void checkFunctionDecl(FunctionDecl node, NodeList<Node> stack) {
				if(node.getName().length() > 0) {
					if(Character.isUpperCase(node.getName().charAt(0)) && !node.isExtern()) {
						throw new OocCompilationError(node, stack,
								"Upper-case function name '"+node.getProtoRepr()
								+"'. Function should always begin with a lowercase letter, e.g. camelCase");
					}
				}
				
				if(!node.isFromPointer()) { 
					String name = node.getName();
					if(node.getTypeDecl() != null) {
						name = node.getTypeDecl().toString() + "." + name;
					}
					
					String suffixedName = node.getName()+"_"+node.getSuffix();
					FunctionDecl other;
					if(node.isMember()) {
						HashMap<String, FunctionDecl> set = classFuncNames.get(node.getTypeDecl());
						if(set == null) {
							set = new HashMap<String, FunctionDecl>();
							classFuncNames.put(node.getTypeDecl(), set);
						}
						other = set.put(suffixedName, node);
					} else {
						other = funcNames.put(suffixedName, node);
					}
					if(other != null) {
						// if either are unversioned, it's an immediate lose
						if(node.getVersion() == null || other.getVersion() == null) {
							throwError(node, other, stack, name);
						}
						// if their version is the same, it's a lose too.
						if(node.getVersion().equals(other.getVersion())) {
							throwError(node, other, stack, name);
						}
					}
				}
			}
			
			void throwError(FunctionDecl node, FunctionDecl other, NodeList<Node> stack, String name)
			throws OocCompilationError {
				if(name.equals("class") && stack.find(CoverDecl.class) != -1) return;
				// FIXME debug
				new OocCompilationError(node, stack,
						"Two functions have the same name '"+name
							+"', add suffix to one of them! (even if they have different signatures). e.g. "
							+name+": func ~suffix "+node.getArgsRepr()+" -> "+node.getReturnType()).printStackTrace();
				throw new OocCompilationError(other, stack, "The other definition is here:");
			}
			
			
			private void checkVariableDecl(VariableDecl node, NodeList<Node> stack) {
				Type varDeclType = node.getType();
				if(varDeclType != null && varDeclType.getRef() != null && !varDeclType.getRef().isExtern()
						&& varDeclType.getName().length() > 0
						&& !(varDeclType.getRef() instanceof TypeParam)
						&& Character.isLowerCase(varDeclType.getName().charAt(0))) {
					throw new OocCompilationError(varDeclType, stack,
							"Variable declaration has type '"+varDeclType.getName()+
							"', which begins with a lowercase letter."+
							" Types should always begin with an uppercase letter, e.g. CamelCase");
				}
				/*
				for(VariableDeclAtom atom: node.getAtoms()) {
					if(atom.getName().length() == 0) continue;
					if(Character.isUpperCase(atom.getName().charAt(0)) && !node.getType().isConst()
							&& node.shouldBeLowerCase()) {
						throw new OocCompilationError(atom, stack,
								"Upper-case variable name '"+atom.getName()+": "+node.getType()
								+"'. Variables should always begin with a lowercase letter, e.g. camelCase");
					}
				}
				*/
			}
			
			private void checkTypeDecl(TypeDecl node, NodeList<Node> stack)
				throws OocCompilationError {
				if(node.isExtern() || node.getName().length() == 0) return;
				if(Character.isLowerCase(node.getName().charAt(0))) {
					throw new OocCompilationError(node, stack,
						"Lower-case type name '"+node.getName()
						+"'. Types should always begin with a capital letter, e.g. CamelCase (stack = "+stack);
				
				}
				
				if(!(node instanceof ClassDecl)) return;
				ClassDecl classDecl = (ClassDecl) node;
				
				if(classDecl.isAbstract()) return;
				
				NodeList<FunctionDecl> functions = new NodeList<FunctionDecl>();
				classDecl.getFunctionsRecursive(functions);
				
				for(FunctionDecl decl: functions) {
					FunctionDecl realDecl = classDecl.getFunction(decl.getName(), decl.getSuffix(), null);
					if(realDecl.isAbstract()) {
						throw new OocCompilationError(classDecl, stack, "Class "+classDecl.getName()
								+" must implement "+decl.getProtoRepr()+", or be declared abstract. Little help: "+decl.getStub());
					}
					
					//ClassDecl baseClass = classDecl.getBaseClass(decl);
					//FunctionDecl baseDecl = baseClass.getFunction(realDecl.getName(), realDecl.getSuffix(), null);
					// TODO check arg types and return type also
					/*
					if(baseDecl != null && realDecl.getArguments().size() != baseDecl.getArguments().size()) {
						throw new OocCompilationError(decl, stack, "Class "+classDecl.getName()
							+" must implement "+decl.getProtoRepr()+" with the same arguments & return type as "
							+baseDecl.getArguments()+". realArgs = "+realDecl.getArguments()
							+", baseArgs = "+baseDecl.getArguments());
					}
					*/
				}
			}
			
			private void checkValuedReturn(ValuedReturn node,
					NodeList<Node> stack) {

				FunctionDecl decl = (FunctionDecl) stack.get(stack.find(FunctionDecl.class));
				if(decl.getReturnType().isVoid()) {
					throw new OocCompilationError(node, stack,
							"Returning a value in function "+decl.getProtoRepr()
								+" which is declared as returning nothing");
				}
				
			}

		}).visit(module);
		
		return false;
		
	}

}
