import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

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


    @Test
    public void testIsQuoteHeader() throws Exception {
        String[] txts = new String[]{
                "On 13 Aug 2006 20:35:18 -0700, \"eclinux94\" <eclinux94@gmail.com> wrote:",
                "On 24 Feb 2006 10:26:14 -0800, \"ChickenMama\n<lauraw@webbtechsolutions.com> wrote:",
                "On Sat, 25 Feb 2006 13:05:07 GMT, \"MARK A  WITTENBORN\"\n<mwitt822@earthlink.net> wrote:",
                "On 06 Apr 2006, Rasta Khan <fkhall@kotm.biz> posted some \nnews:Xns979EDF98AA3795174826947@198.186.190.225:",
                "On Sun, 09 Apr 2006 00:52:34 GMT, Dennis\n<dennis32542@SPAMSAVERsbcglobal.net> wrote:",
                "On 16 Apr 2006, Nihil <x@y.invalid> posted some\nnews:lwo5fcv277cc$.pctg5t6osp3r$.dlg@40tude.net:",
                "On 14 Apr 2006, Joseph Bartlo <jbartlo@verizon.net> posted some\nnews:4440657F.AA59CA05@verizon.net:",
                "On Sat, 13 May 2006 07:14:36 +1000, \"regn pickford\"\n<doregn.pickford@idl.not.au> wrote:",
                "On Mon, 13 Sep, we will meet in the school yard."
        };

        boolean[] expectedOutcomes = new boolean[]{true, true, true, true, true, true, true, true, false};

        for (int i = 0; i < expectedOutcomes.length; i++){
            assertEquals(txts[i], expectedOutcomes[i], TextUtil.isQuoteHeader(txts[i]));
        }


    }

    @Test
    public void testReplaceInvalidChar() throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("START ");
        builder.append((char) 239);
        builder.append((char) 191);
        builder.append((char) 189);
        builder.append(" END");


        String replaced = TextUtil.replaceInvalidChar(builder.toString());
        assertEquals("START \0 END", replaced);
    }
}