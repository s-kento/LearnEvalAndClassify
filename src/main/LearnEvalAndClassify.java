package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import evaluate.CrossValidation;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Classifier;
//import evaluate.CrossValidation;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
//import weka.core.Instances;
//import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.Filter;

public class LearnEvalAndClassify {
	static Map<Integer,Integer> filevec;
	static int ValNum[];
	final static String directoryPath = "data\\ARFF";
	/**cross validationの結果格納先．この中に，cvsと各結果を格納するフォルダが作られる*/
	final static String path = "data\\res\\resCV\\";
	final static String path2= "data\\res\\resCP\\"; //クロスプロジェクト評価結果書き込み用
	final static String[] algorithmList = {"J48","RandomForest","NaiveBayes","SMO"};
	final static int runs = 100;
	final static int folds =  10;
	static String pathCP;

	public static File csvFile;
	public static FileWriter csvFileWriter;
	public static BufferedWriter bw;
	public static PrintWriter pw;

	private static Map<String, Classifier> classifiers;

	static List<File> fileList = new ArrayList<File>();

	public static void main(String[] args) throws IOException {
		//交差検証，特徴選択，モデル構築，クロスプロジェクトに関する調査を行う．

		DataSource source;
		try {
			//ファイルリストをfileListに取得
			File directory = new File(directoryPath);
			if(directory.exists())
				makeFileList(directory);

			//手法リストはalgolithmListに格納

			//ファイルリストのデータそれぞれを，各アルゴリズムで交差検証
			System.out.println("------CrossValidation--------");
			CrossValidation cv = new CrossValidation(path, fileList, algorithmList, runs, folds);
			cv.evalate();

			System.out.println("------Finished--------\n");

			//各学習データについて，フィルタリングして，モデル構築，クロスプロジェクトの評価を行う．
			/*
			System.out.println("-----Evaluate CrossProject------");
			Instances data;
			Instances filteredData;

			for(File fileData:fileList){

				source = new DataSource(fileData.getCanonicalPath());
				data = source.getDataSet();
				if (data.classIndex() == -1)
					data.setClassIndex(data.numAttributes()-1);
				filteredData=null;

				//特徴選択
				atsel(data);

				//フィルタリング : 特徴選択してデータを加工
				filteredData=useFilter(data);


				evalCrossProject(filteredData,fileData);
			}
			System.out.println("------Finished--------\n");
	*/
			System.out.println("\nfinished all task.\n");

		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	protected static void atsel(Instances data) throws Exception {
		//特徴選択
		// setting class attribute if the data format does not provide this information
		// For example, the XRFF format saves the class attribute information as well
		if (data.classIndex() == -1)
		  data.setClassIndex(data.numAttributes() - 1);
		System.out.println("1.Attribute selection");
		AttributeSelection attsel = new AttributeSelection();
		CfsSubsetEval subeval =new CfsSubsetEval();
		BestFirst search = new BestFirst();
		String[] Options= {"-D","1","-N","5"};	//BestFirst用
		search.setOptions(Options);
		attsel.setEvaluator(subeval);
		attsel.setSearch(search);
		attsel.SelectAttributes(data);
		int[] indices = attsel.selectedAttributes();
		for (int i = 0; i < indices.length; i++)indices[i]++;

		System.out.println("selected attributes");
		System.out.println(Utils.arrayToString(indices));
	}

	protected static Instances useFilter(Instances data) throws Exception {
		System.out.println("Filtering data.");
	    weka.filters.supervised.attribute.AttributeSelection filter = new weka.filters.supervised.attribute.AttributeSelection();
	    CfsSubsetEval eval = new CfsSubsetEval();
	    BestFirst search = new BestFirst();
	    String[] Options= {"-D","1","-N","5"};
	    search.setOptions(Options);

	    filter.setEvaluator(eval);
	    filter.setSearch(search);
	    filter.setInputFormat(data);
	    Instances newData = Filter.useFilter(data, filter);
	    if (newData.classIndex() == -1)
	    	newData.setClassIndex(newData.numAttributes()-1);
	    return newData;
	    //System.out.println(newData);
	  }

	private static void cvaridation() throws Exception{

		//cv.evalate("BayesNet");
		//cv.evalate("J48");
		//cv.evalate("RandomForest");
		//cv.evalate("NaiveBayes");
		//cv.evalate("SMO");
		//cv.evalate("Logistic");
	}
	/*
	private static void evalCrossProject(Instances data,File file) throws Exception{
		makedirParent(path2);
		csvFile = new File(path2+"resCP.csv");	//クロスプロジェクト評価結果書き込み用
		csvFileWriter = new FileWriter(csvFile);
		bw = new BufferedWriter(csvFileWriter);
		pw = new PrintWriter(bw);

		AttributeSelectedClassifier cls = new AttributeSelectedClassifier();

		DataSource sourceCP;
		//Instances dataCP;
		String[] options;

		pw.print("data|Algorithm");
		for (String st : algorithmList) {
			pw.print(","+st);
		}
		pw.println();
		pw.print(removeFileExtension(file.getName()));

		//-----------------モデル構築-----------------------
		for (String st : algorithmList) {
			//処理プロジェクト仁応じてclsを設定
			if (st.equals("J48")) {
				J48 base = new J48();
				options =Options.J48;
				cls.setClassifier(base);
				cls.setOptions(options);
				//pathCP="CrossProject\\j48.arff";

			} else if (st.equals("SMO")) {
				SMO base = new SMO();
				options = Options.SMO;
				cls.setClassifier(base);
				cls.setOptions(options);
				//pathCP="CrossProject\\SMO.arff";
			} else if (st.equals("BayesNet")) {
				BayesNet base = new BayesNet();
				options = Options.BayesNet;
				//Discretize m_DiscretizeFilter = new Discretize();
				//m_DiscretizeFilter.setInputFormat(data);
				//data = Filter.useFilter(data, m_DiscretizeFilter);
				//System.out.println("discretized");
				cls.setClassifier(base);
				cls.setOptions(options);
				//pathCP="CrossProject\\bayesnet.arff";
			} else if (st.equals("NaiveBayes")) {
				NaiveBayes base = new NaiveBayes();
				options = Options.NaiveBayes;
				cls.setClassifier(base);
				cls.setOptions(options);
				pathCP="CrossProject\\naivebayes.arff";
			} else if (st.equals("Logistic")) {
				Logistic base = new Logistic();
				options = Options.Logistic;
				cls.setClassifier(base);
				cls.setOptions(options);
				//pathCP="CrossProject\\logistic.arff";
			} else if (st.equals("ZeroR")) {
				ZeroR base = new ZeroR();
				options = Options.ZeroR;
				cls.setClassifier(base);
				cls.setOptions(options);
				//pathCP="CrossProject\\zeror.arff";
			} else if (st.equals("RandomTree")) {
				RandomTree base = new RandomTree();
				options = Options.RandomTree;
				cls.setClassifier(base);
				cls.setOptions(options);
				//pathCP="CrossProject\\rondomtree.arff";
			} else if (st.equals("RandomForest")) {
				RandomForest base = new RandomForest();
				options = Options.RandomForest;
				cls.setClassifier(base);
				cls.setOptions(options);
				//pathCP="CrossProject\\randomforest.arff";
			} else {
				System.err.println("分類器は存在しない");
				System.exit(0);
			}
			cls.buildClassifier(data);
		}

		//-----------モデル構築ここまで------------------

		//まずベースとなる手法を選択
		for (String st1 : algorithmList) {

			//処理プロジェクト仁応じてclsを設定
			if (st1.equals("J48")) {
				J48 base = new J48();
				options =Options.J48;
				cls.setClassifier(base);
				cls.setOptions(options);
				//pathCP="CrossProject\\j48.arff";

			} else if (st1.equals("SMO")) {
				SMO base = new SMO();
				options = Options.SMO;
				cls.setClassifier(base);
				cls.setOptions(options);
				pathCP="CrossProject\\SMO.arff";
			} else if (st1.equals("BayesNet")) {
				BayesNet base = new BayesNet();
				options = Options.BayesNet;
				//Discretize m_DiscretizeFilter = new Discretize();
				//m_DiscretizeFilter.setInputFormat(data);
				//data = Filter.useFilter(data, m_DiscretizeFilter);
				//System.out.println("discretized");
				cls.setClassifier(base);
				cls.setOptions(options);
				//pathCP="CrossProject\\bayesnet.arff";
			} else if (st1.equals("NaiveBayes")) {
				NaiveBayes base = new NaiveBayes();
				options = Options.NaiveBayes;
				cls.setClassifier(base);
				cls.setOptions(options);
				pathCP="CrossProject\\naivebayes.arff";
			} else if (st1.equals("Logistic")) {
				Logistic base = new Logistic();
				options = Options.Logistic;
				cls.setClassifier(base);
				cls.setOptions(options);
				//pathCP="CrossProject\\logistic.arff";
			} else if (st1.equals("ZeroR")) {
				ZeroR base = new ZeroR();
				options = Options.ZeroR;
				cls.setClassifier(base);
				cls.setOptions(options);
				//pathCP="CrossProject\\zeror.arff";
			} else if (st1.equals("RandomTree")) {
				RandomTree base = new RandomTree();
				options = Options.RandomTree;
				cls.setClassifier(base);
				cls.setOptions(options);
				//pathCP="CrossProject\\rondomtree.arff";
			} else if (st1.equals("RandomForest")) {
				RandomForest base = new RandomForest();
				options = Options.RandomForest;
				cls.setClassifier(base);
				cls.setOptions(options);
				//pathCP="CrossProject\\randomforest.arff";
			} else {
				System.err.println("分類器は存在しないっ");
				System.exit(0);
			}
			dataCP = DataSource.read(pathCP);
			if (data.classIndex() == -1)
				data.setClassIndex(data.numAttributes()-1);
			cls.buildClassifier(data);

			//ベースプロジェクトそれぞれに対して，ほかのプロジェクトデータで評価し，結果を出力．
			for (File file2 : fileList) {


				for (String st2 : algorithmList) {
					//処理プロジェクトに応じてデータをロードし，clsを設定

					sourceCP = new DataSource(pathCP);
					dataCP = sourceCP.getDataSet();

					//pw.println(" ,bayesnet,J48,RF,naivebayes");

					//Classifier cls2 = new J48();
					//cls2.buildClassifier(path[1]);
					//Evaluation eval= new Evaluation(dataCP[2]);
					//System.out.println();
				}
			}
		}
	}*/


	private static void makeFileList(File file) {
		if (file.isDirectory()) {
			File[] innerFiles = file.listFiles();
			for (File tmp : innerFiles) {
				makeFileList(tmp);
			}
		} else if (file.isFile()) {
			if (file.getName().endsWith(".arff")) {
				fileList.add(file);
			}
		}
	}

	public static void makedirParent(String filePath){
	    File file = new File(filePath);
	    makedir(file.getParent());
	}

	public static void makedir(String dirPath){
	    File dir = new File(dirPath);
	    if(!dir.exists()){
	        dir.mkdirs();
	    }
	}
	public static String removeFileExtension(String filename) {
	    int lastDotPos = filename.lastIndexOf('.');

	    if (lastDotPos == -1) {
	      return filename;
	    } else if (lastDotPos == 0) {
	      return filename;
	    } else {
	      return filename.substring(0, lastDotPos);
	    }
	  }
}
