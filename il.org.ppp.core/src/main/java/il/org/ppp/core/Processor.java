package il.org.ppp.core;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

@SupportedAnnotationTypes(value = { "*" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class Processor extends AbstractProcessor {
	@Override
	public synchronized void init(ProcessingEnvironment e) {
		super.init(e);
		e.getMessager().printMessage(Kind.NOTE, "wow");
	}

	@Override
	public boolean process(Set<? extends TypeElement> ans, RoundEnvironment e) {
		return false;
	}
}
