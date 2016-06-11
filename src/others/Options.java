package others;


public class Options {

	/**
	 * binarySplits -- 名義属性の分割に二分分割を使うか（木を構築するときに）． <br>
	 * confidenceFactor -- 信頼度は枝刈りに対して利用しました．(より小さい値はより多く枝刈りを行います)<br>
	 * debug -- trueに設定されると，分類学習アルゴリズムは追加インフォメーションをコンソールに出力します． <br>
	 * minNumObj -- 葉あたりの最小事例数 <br>
	 * numFolds -- 誤り低減枝刈りのために利用するデータの量を決定してください． 1つの折り目部分集合は枝刈りのために利用され，残りは決定木の生成に利用します． <br>
	 * reducedErrorPruning -- 誤り低減枝刈りをＣ４．５方式の枝刈りの変わりに使用するかどうか． <br>
	 * saveInstanceData -- 訓練データを可視化のために保存するかどうか． <br>
	 * seed -- データをランダム化するのに種が利用されます．誤りを低減させるような枝刈りを利用するときに． <br>
	 * subtreeRaising -- 部分木の出現操作を枝刈りの際に考慮するかどうか． <br>
	 * unpruned -- 枝刈りを実行するかどうか． <br>
	 * useLaplace -- ラプラスに基づいて葉での数え上げが平滑化されるかどうかです． <br>
	 */

	public static final String[] J48 = { "-U" };

	public static final String[] SMO = {};

	public static final String[] BayesNet = {};

	public static final String[] NaiveBayes = {};

	public static final String[] Logistic = {};

	public static final String[] ZeroR = {};

	public static final String[] RandomTree = {};

	public static final String[] RandomForest = {};

}
