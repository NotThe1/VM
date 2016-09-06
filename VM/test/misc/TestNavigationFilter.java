package misc;

import javax.swing.text.Element;
import javax.swing.text.NavigationFilter;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;

public class TestNavigationFilter extends NavigationFilter {

	private int dataStart = 0;
	private int dataEnd = 0;
	private int asciiStart = 0;
	private int asciiEnd = 0;
	private StyledDocument doc;

	public TestNavigationFilter(StyledDocument doc) {
		this.doc = doc;
		Element paragraphElement = doc.getParagraphElement(0);

		Element dataElement = paragraphElement.getElement(1);
		this.dataStart = dataElement.getStartOffset() + 1;
		this.dataEnd = dataElement.getEndOffset()-1;

		dataElement = paragraphElement.getElement(2);
		this.asciiStart = dataElement.getStartOffset();
		this.asciiEnd = dataElement.getEndOffset();

	}// Constructor

	public void setDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias) {
		Element paragraphElement = doc.getParagraphElement(dot);
		System.out.printf("[setDot] paragraphElement - start %d, end %d%n", paragraphElement.getStartOffset(),
				paragraphElement.getEndOffset());

		int limit = paragraphElement.getStartOffset() + dataStart;
		int limitWrap = paragraphElement.getStartOffset() + dataEnd;
		
		if (dot < limit) {
			fb.setDot(limit, bias);
		}else if(dot >= limitWrap){
			fb.setDot(paragraphElement.getEndOffset()+ dataStart, bias);
		} else {
			fb.setDot(dot, bias);
		}//
		System.out.printf("[setDot] dot: %d, limit: %d%n", dot, limit);
	}//

	public void moveDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias) {
		if (dot < dataStart) {
			fb.setDot(dataStart, bias);
		} else {
			fb.setDot(dot, bias);
		}//
		System.out.printf("[moveDot] **** dot: %d, dataStart: %d%n", dot, dataStart);

	}// moveDot

}// class TestNavigationFilter
