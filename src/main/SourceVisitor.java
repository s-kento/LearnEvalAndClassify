package main;

//import java.util.ArrayList;
import jp.ac.osaka_u.ist.sdl.prevol.data.VectorData;
import jp.ac.osaka_u.ist.sdl.prevol.methodanalyzer.NodeTypeCounter;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
//import org.eclipse.jdt.core.dom.MethodDeclaration;

public class SourceVisitor extends ASTVisitor {

	CompilationUnit root;

	//private List<MethodData> methodList;
	public VectorData vector;

	public SourceVisitor(CompilationUnit root) {
		this.root = root;
		//this.methodList = new ArrayList<MethodData>();
	}

	@Override
	public boolean visit(CompilationUnit node) {
		constSourceData(node);
		return true;
	}

	private void constSourceData(CompilationUnit node) {
		SourceData source = new SourceData();
		source.setProgram(node.toString());

		/**
		 * 特徴量の取得
		 */
		final NodeTypeCounter counter = new NodeTypeCounter();
		node.accept(counter);
		vector = counter.getVectorData();//ベクトルデータの取得

		source.setVector(vector);

		//methodList.add(method);
	}
	/*
	public List<MethodData> getMethodList() {
		return methodList;
	}*/

}
