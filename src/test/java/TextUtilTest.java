import org.junit.Test;
import org.w3c.dom.Text;

import java.util.List;

import static org.junit.Assert.*;

public class TextUtilTest {

    @Test
    public void testFindParagraphs() throws Exception {
        String textWithParas1 = "aaa\n\nbbb\nbbb\n\tccc";
        List<String> paras1 = TextUtil.findParagraphs(textWithParas1);
        assertEquals(3, paras1.size());
        assertEquals("aaa", paras1.get(0));
        assertEquals("bbb\nbbb", paras1.get(1));
        assertEquals("\tccc", paras1.get(2));
    }

    @Test
    public void testStripQuotes() throws Exception {
        String quotedText1 = "> qqq\n> qqq\nzzz\n> qqq\nabc def\n";

        assertEquals("abc def\n", TextUtil.stripQuotes(quotedText1));

    }
}