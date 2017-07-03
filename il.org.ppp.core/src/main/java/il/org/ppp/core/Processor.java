package il.org.ppp.core;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes(value = { "*" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class Processor extends AbstractProcessor {
	@Override
	public synchronized void init(ProcessingEnvironment e) {
		super.init(processingEnv);
		try (Writer w = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("C:/Users/OriRoth/Desktop/a.txt"), "utf-8"))) {
			w.write("@@@");
		} catch (IOException x) {
			x.printStackTrace();
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> ans, RoundEnvironment e) {
		return false;
	}
}
