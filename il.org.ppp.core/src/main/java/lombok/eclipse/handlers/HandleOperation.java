package lombok.eclipse.handlers;

import static lombok.eclipse.handlers.EclipseHandlerUtil.*;
import static org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants.*;
import static lombok.eclipse.Eclipse.*;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

import lombok.Operation;
import lombok.core.AnnotationValues;
import lombok.core.LombokImmutableList;
import lombok.core.AST.Kind;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import ppp.util.AnnotationReflection;

@SuppressWarnings("restriction")
public class HandleOperation extends EclipseAnnotationHandler<Operation> {
	@Override
	public void handle(AnnotationValues<Operation> annotation, Annotation ast, EclipseNode annotationNode) {
		EclipseNode operation = annotationNode.up();
		LombokImmutableList<EclipseNode> components = operation.down();
		List<EclipseNode> concrete = new LinkedList<>(), defualt = new LinkedList<>();
		EclipseNode value = null;
		String valueName = AnnotationReflection.getString(Operation.class, "valueName"),
				applyName = AnnotationReflection.getString(Operation.class, "applyName");
		for (EclipseNode n : components)
			if (Kind.METHOD.equals(n.getKind()))
				if (valueName.equals(n.getName()))
					value = n;
				else if (((MethodDeclaration) n.get()).isDefaultMethod())
					concrete.add(n);
				else
					defualt.add(n);
		ASTNode source = annotationNode.get();
		TypeDeclaration od = (TypeDeclaration) operation.get();
		MethodDeclaration baseApply = createApply(od, concrete, source, applyName);
		injectMethod(operation, baseApply);
		annotationNode.addWarning("hmm...");
	}

	private MethodDeclaration createApply(TypeDeclaration operation, List<EclipseNode> methods, ASTNode source,
			String applyName) {
		// TODO Roth: utility for empty method etc.
		MethodDeclaration $ = new MethodDeclaration(operation.compilationResult);
		$.sourceStart = source.sourceStart;
		$.sourceEnd = source.sourceEnd;
		$.modifiers = AccPublic | AccStatic;
		$.returnType = createType(operation, getPosNom(source.sourceStart, source.sourceEnd));
		$.annotations = null;
		$.arguments = getArguments(methods, source);
		$.selector = applyName.toCharArray();
		$.binding = null;
		$.thrownExceptions = null;
		$.typeParameters = null;
		$.bits |= ECLIPSE_DO_NOT_TOUCH_FLAG;
		$.bodyStart = $.declarationSourceStart = $.sourceStart = source.sourceStart;
		$.bodyEnd = $.declarationSourceEnd = $.sourceEnd = source.sourceEnd;
		$.statements = createApplyBody(operation, $.arguments, source, $.compilationResult);
		return $;
	}

	private Statement[] createApplyBody(TypeDeclaration operation, Argument[] arguments, ASTNode source,
			CompilationResult cr) {
		int pS = source.sourceStart, pE = source.sourceEnd;
		QualifiedAllocationExpression $ = new QualifiedAllocationExpression();
		// Extra flag needed?
		$.bits |= ECLIPSE_DO_NOT_TOUCH_FLAG | Expression.InsideExpressionStatement;
		$.sourceStart = pS;
		$.sourceEnd = $.statementEnd = pE;
		$.type = copyType(createType(operation, getPosNom(pS, pE)), source);
		$.enclosingInstance = null;
		$.anonymousType = createApplyMethods(arguments, source, $, cr, operation);
		return new Statement[] { new ReturnStatement($, pS, pE) };
	}

	private TypeDeclaration createApplyMethods(Argument[] arguments, ASTNode source,
			QualifiedAllocationExpression parent, CompilationResult cr, TypeDeclaration enclosure) {
		TypeDeclaration $ = new TypeDeclaration(cr);
		$.bits |= ECLIPSE_DO_NOT_TOUCH_FLAG;
		int pS = source.sourceStart, pE = source.sourceEnd;
		$.sourceStart = $.declarationSourceStart = $.modifiersSourceStart = $.bodyStart = pS;
		$.sourceEnd = $.declarationSourceEnd = $.bodyEnd = pE;
		$.name = anonymousName(enclosure);
		// TODO Roth: Is it needed?
		$.enclosingType = enclosure;
		// TODO Roth: Is it needed?
		$.allocation = parent;
		$.methods = null;
		$.fields = null;
		$.methods = new MethodDeclaration[arguments.length];
		for (int i = 0; i < $.methods.length; ++i) {
			MethodDeclaration m;
			$.methods[i] = m = new MethodDeclaration($.compilationResult);
			m.sourceStart = pS;
			m.sourceEnd = pE;
			m.modifiers = AccPublic;
			m.returnType = copyType(arguments[i].type, source);
			m.annotations = null;
			m.arguments = null;
			m.selector = arguments[i].name;
			m.binding = null;
			m.thrownExceptions = null;
			m.typeParameters = null;
			m.bits |= ECLIPSE_DO_NOT_TOUCH_FLAG;
			m.bodyStart = $.declarationSourceStart = $.sourceStart = source.sourceStart;
			m.bodyEnd = $.declarationSourceEnd = $.sourceEnd = source.sourceEnd;
			m.statements = new Statement[] {
					new ReturnStatement(new SingleNameReference(arguments[i].name, getPosNom(pS, pE)), pS, pE) };
		}
		return $;
	}

	private Argument[] getArguments(List<EclipseNode> methods, ASTNode source) {
		Argument[] $ = new Argument[methods.size()];
		int pS = source.sourceStart, pE = source.sourceEnd;
		for (int i = 0; i < $.length; ++i) {
			MethodDeclaration m = (MethodDeclaration) methods.get(i).get();
			Argument a = $[i] = new Argument(m.selector, getPosNom(pS, pE), copyType(m.returnType, source),
					Modifier.FINAL);
			a.sourceStart = pS;
			a.sourceEnd = pE;
		}
		return $;
	}

	// TODO Roth: move to utility
	// TODO Roth: fix
	private TypeReference createType(TypeDeclaration operation, long pos) {
		// Maybe pos=0 is wrong?
		return new SingleTypeReference(operation.name, pos);
	}

	// TODO Roth: move to utility
	private long getPosNom(int pS, int pE) {
		return (long) pS << 32 | pE;
	}

	// TODO Roth: move to utility
	// TODO Roth: fix
	private char[] anonymousName(TypeDeclaration enclosure) {
		return (new String(TypeConstants.ANONYM_PREFIX) + new String(enclosure.name)
				+ new String(TypeConstants.ANONYM_SUFFIX)).toCharArray();
	}
}
