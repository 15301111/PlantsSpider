import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;

/** 
* @author: 张子开
* @date：2017年4月7日 下午3:00:36
* @version 1.0 
*/

public class PdfPraser{

	public static void main(String[] args) throws InvalidPasswordException, IOException {
		PDDocument document = PDDocument.load(new File("F://test.pdf"));
		PDFTextStripper stripper = new PDFTextStripper();
		final String wordSeparator = stripper.getWordSeparator();
		final String text = stripper.getText(document);
		System.out.println(text);
	}
}
