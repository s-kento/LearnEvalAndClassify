package evaluate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

import others.Options;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Range;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;

public class CrossValidation {

	private String pathofRes;

	private int runs;
	private int folds;

	private double sumPrecision = 0;
	private double sumRecall = 0;

	private File csvFile;
	private FileWriter csvFileWriter;
	private BufferedWriter bw;
	/**CV結果をCSVに書き込む用*/
	private PrintWriter pw;

	private List<File> fileList ;
	private String[] algorithmList;

	private File cvSummary;
	private FileWriter cvSummaryWriter;
	private BufferedWriter bw2;
	/** 交差検証ごとのSummary出力用 */
	private PrintWriter pw2;

	public CrossValidation(String path, List<File> filelist, String[] algorithmlist,  int runs, int folds) throws IOException {
		pathofRes = path;
		this.runs = runs;
		this.folds = folds;
		this.fileList=filelist;
		this.algorithmList=algorithmlist;

		//設定までやっておくので，適当に書き込んで．
		//evalateでデータだけは出力するので．

		//pathまでのディレクトリを作る．
		makedirParent(pathofRes+"resCV");
		//pw : CV結果書き込み用
		csvFile = new File(pathofRes+"resCV.csv");
		csvFileWriter = new FileWriter(csvFile);
		bw = new BufferedWriter(csvFileWriter);
		pw = new PrintWriter(bw);

	}

	/*protected void finalize() throws Throwable {
	    super.finalize();
	    //csvファイルクローズ

	}*/

	/**
	 * ファイルリストの各データをアルゴリズムリストの各手法で交差検証
	 * @param st String of Algorithm
	 * @throws Exception
	 */
	public void evalate() throws Exception {
		/*
		File cvSummary= new File("Summaryof"+st+".txt");
		FileWriter cvSummaryWriter = new FileWriter(cvSummary);
		BufferedWriter bw2 = new BufferedWriter(cvSummaryWriter);
		PrintWriter pw2 = new PrintWriter(bw2);
		*/
		/**arffデータ取得用*/
		Instances data;

		//学習器とそのオプション
		Classifier cls = null;
		String[] options = null;

		//交差検証法のときにつかう
		Instances train;
		Instances test;
		Evaluation eval;
		StringBuffer getpred= new StringBuffer();
		StringBuffer test2;
		//凡例部分書き込み
		pw.print("data|Algorithm");
		for (String st : algorithmList) {
			pw.print(","+st);
		}
		pw.println();
		//すべてのARFFデータに対して実行
		for (File fileData : fileList) {
			System.out.println("******"+fileData.getName()+"******");
			//まずファイル名書き込み
			pw.print(fileData.getName());

			//fileDataのsummary収納用ディレクトリ作成
			File mkdir = new File(pathofRes+removeFileExtension(fileData.getName()));
			mkdir.mkdirs();

			//fileDataから学習データ読み込み
			data = DataSource.read(fileData.getAbsolutePath().toString());
			if (data.classIndex() == -1)
				data.setClassIndex(data.numAttributes() - 1);

			//特徴選択しておく
			data = useFilter(data);
			if (data.classIndex() == -1)
		    	data.setClassIndex(data.numAttributes()-1);

			//removeSmall(data);

			//すべての手法についてCVを実行．CSVにまとめ，それぞれのSummaryを出力する
			for (String stAlgo : algorithmList) {
				System.out.println("*** " + stAlgo + " ***");
				long start = System.currentTimeMillis();
				cvSummary= new File(pathofRes+removeFileExtension(fileData.getName())+"\\Summary-" + removeFileExtension(fileData.getName()) + "-" + stAlgo + ".txt");
				cvSummaryWriter = new FileWriter(cvSummary);
				bw2 = new BufferedWriter(cvSummaryWriter);
				pw2 = new PrintWriter(bw2);

				sumPrecision = 0;
				sumRecall = 0;



				//学習器を設定
				if (stAlgo.equals("J48")) {
					cls = new J48();
					options = Options.J48;
				} else if (stAlgo.equals("SMO")) {
					cls = new SMO();
					options = Options.SMO;
				} else if (stAlgo.equals("BayesNet")) {
					cls = new BayesNet();
					options = Options.BayesNet;
					Discretize m_DiscretizeFilter = new Discretize();
				      m_DiscretizeFilter.setInputFormat(data);
				      data = Filter.useFilter(data, m_DiscretizeFilter);
				      System.out.println("discretized");
				} else if (stAlgo.equals("NaiveBayes")) {
					cls = new NaiveBayes();
					options = Options.NaiveBayes;
				} else if (stAlgo.equals("Logistic")) {
					cls = new Logistic();
					options = Options.Logistic;
				} else if (stAlgo.equals("ZeroR")) {
					cls = new ZeroR();
					options = Options.ZeroR;
				} else if (stAlgo.equals("RandomTree")) {
					cls = new RandomTree();
					options = Options.RandomTree;
				} else if (stAlgo.equals("RandomForest")) {
					cls = new RandomForest();
					options = Options.RandomForest;
				} else {
					System.err.println("分類器は存在しないっ");
					System.exit(0);
				}
				cls.setOptions(options);

				/*
				 Evaluation eval = new Evaluation(data);
				 eval.crossValidateModel(cls, data, 10, new Random(1));*/
				 //System.out.println(evaluation.toSummaryString());

				int seed = 5;
				//cls.buildClassifier(data);
				//System.out.println(cls);
				//特徴選択済みのデータを読み込ませて，交差検証．
				//String[] optionsForEval = {"-p",new Integer(data.numInstances()).toString()};

				//予測結果取得のための記述
				getpred = new StringBuffer();
				test2=new StringBuffer();
				test2.append("1");
				for (int i = 2; i <= data.numAttributes(); i++) {
					test2.append(","+ i);
				}
				Range range = new Range(test2.toString());
				eval = new Evaluation(data);
				eval.crossValidateModel(cls, data, folds, new Random(seed),getpred,range,true);
				//System.out.println(getpred.toString());

				long stop =  System.currentTimeMillis();
				System.out.println("実行にかかった時間は"+(stop-start)+"ミリ秒です");
				//eval.crossVali....との違いがわからん
				/*
				Random rand = new Random(seed);

				Instances randData = new Instances(data);
				randData.randomize(rand);

				//離散化してないWarning対策(BayesNetのみ
				//if (randData.classAttribute().isNominal())
				//	randData.stratify(folds);
				//eval = new Evaluation(randData);
				Classifier clsCopy;
				for (int n = 0; n < folds; n++) {
					train = randData.trainCV(folds, n);
					test = randData.testCV(folds, n);

					clsCopy = Classifier.makeCopy(cls);
					clsCopy.buildClassifier(train);
					eval.evaluateModel(clsCopy, test);
				}*/


				//表の出力
				pw.print("," + eval.pctCorrect());
				//System.out.println(eval.toSummaryString());

				pw2.println(eval.toSummaryString());
				pw2.println(eval.toMatrixString());
				pw2.println(eval.toClassDetailsString());
				pw2.write(getpred.toString());
				pw2.close();
				//交差検証法を複数回実行したくなったらこれを使うとよい
				/*for (int i = 0; i < runs; i++) {
					int seed = i + 1;
					Random rand = new Random(seed);
					Instances randData = new Instances(data);
					randData.randomize(rand);
					if (randData.classAttribute().isNominal())
						randData.stratify(folds);
					Evaluation eval = new Evaluation(randData);
					for (int n = 0; n < folds; n++) {
						Instances train = randData.trainCV(folds, n);
						Instances test = randData.testCV(folds, n);

						Classifier clsCopy = Classifier.makeCopy(cls);
						clsCopy.buildClassifier(train);
						eval.evaluateModel(clsCopy, test);
					}

					// System.out.println();
					// System.out.println("=== Setup run " + (i + 1) + " ===");
					// System.out.println("Classifier: " + cls.getClass().getName() + " " + Utils.joinOptions(cls.getOptions()));
					// System.out.println("Dataset: " + data.relationName());
					// System.out.println("Folds: " + folds);
					// System.out.println("Seed: " + seed);
					// System.out.println();
					// System.out.println(eval.toSummaryString("=== " + folds + "-fold Cross-validation run " + (i + 1) + "===", false));
					// System.out.println(eval.toClassDetailsString("=== ClassDetails ==="));
					// System.out.println(eval.toMatrixString("=== Matrix ==="));

					sumPrecision += eval.precision(0);
					sumRecall += eval.recall(0);
				}*/

				/*double avePrecision = sumPrecision / runs;
				double aveRecall = sumRecall / runs;

				System.out.println("Precision: " + avePrecision);
				System.out.println("Recall: " + aveRecall);*/

			}
			System.out.println(fileData.getName() + " : finished.\n");
			pw.println();
		}
		pw.close();
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
	public static void removeSmall(Instances insts){
		for(int i=0;i<insts.numInstances();i++){
				if(!isBig(insts.instance(i))){
					insts.instance(i).setMissing(0);
				}
		}
		insts.deleteWithMissing(0);
	}
	public static boolean isBig(Instance inst){
		boolean big=false;
		for(int i=0;i<inst.numAttributes()-1;i++){
			if(inst.value(i)>=15)
				big=true;
		}
		return big;
	}
}
