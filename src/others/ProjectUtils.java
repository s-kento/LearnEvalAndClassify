package others;

public class ProjectUtils {

	public static String getFullProjectName(String st) {
		if (st.equalsIgnoreCase("ant"))
			return "ant";
		else if (st.equalsIgnoreCase("argouml"))
			return "argouml";
		else if (st.equalsIgnoreCase("jedit"))
			return "jEdit";
		else if (st.equalsIgnoreCase("jfreechart"))
			return "jFreeChart";
		else if (st.equalsIgnoreCase("mylyn"))
			return "org.eclipse.mylyn.tasks";
		else if (st.equalsIgnoreCase("struts"))
			return "struts";
		else
			return null;
	}

}
