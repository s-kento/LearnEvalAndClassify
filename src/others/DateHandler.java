package others;

import java.util.Date;

public class DateHandler {

	int hour;

	int min;

	int sec;


	public DateHandler(Date start, Date end) {
		init((int) (end.getTime() - start.getTime()));
	}

	public DateHandler(long time) {
		System.out.println(time);
		init((int) time);
	}

	private void init(int time) {
		time = time / 1000;

		// hour = time / 3600;
		hour = 0;
		time = time - hour * 3600;
		min = time / 60;
		sec = time - min * 60;
	}

	public void printTime(String message) {
		System.out.println(min + "•ª" + sec + "•b" + " : " + message);
	}

	public int getHour() {
		return hour;
	}

	public int getMin() {
		return min;
	}

	public int getSec() {
		return sec;
	}

}
