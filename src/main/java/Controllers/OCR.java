package Controllers;

import org.bytedeco.javacpp.BytePointer;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.Languages;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.spelling.SpellingCheckRule;


import java.io.IOException;
import java.util.List;

import static org.bytedeco.javacpp.lept.*;
import static org.bytedeco.javacpp.tesseract.TessBaseAPI;

/**
 * Created by arxa on 27/4/2017.
 */

public class OCR
{
    public static String applyOCR(String matPath)
    {
        BytePointer outText;

        TessBaseAPI api = new TessBaseAPI();
        // Initialize tesseract-applyOCR with English, without specifying tessdata path
        if (api.Init("src/main/resources/", "ENG") != 0) {
            System.err.println("Could not initialize tesseract.");
            System.exit(1);
        }
        //api.SetVariable("tessedit_char_whitelist", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");

        // Open input image with leptonica library
        PIX image = pixRead(matPath);
        api.SetImage(image);


        // Get OCR result
        outText = api.GetUTF8Text();
        if (outText == null) {
            System.out.println("OCR TEXT IS NULL");
            api.End();
            pixDestroy(image);
            return "";
        }
        String ocr_text = outText.getString();
        System.out.println("OCR output:\n" + ocr_text);

        // Destroy used object and release memory
        api.End();
        outText.deallocate();
        pixDestroy(image);



        String[] words = ocr_text.split(" ");
        JLanguageTool languageTool = new JLanguageTool(new BritishEnglish());
        List<RuleMatch> matches;


        for (String word : words){
            try {
                matches = languageTool.check(word);
                if (matches.isEmpty()){
                    if (word.length() > 1){
                        //System.out.println("Native word: "+word);
                    }
                    continue;
                }
                for (RuleMatch match : matches) {
                    if (!match.getSuggestedReplacements().isEmpty()){
                        //System.out.println("Suggestions: ");
                        for (int i = 1; i < match.getSuggestedReplacements().size(); i++){
                            //System.out.println(match.getSuggestedReplacements().get(i));
                        }
                    }
                }
            } catch (IOException e) {
                break;
            }
        }

        return ocr_text;
    }
}
