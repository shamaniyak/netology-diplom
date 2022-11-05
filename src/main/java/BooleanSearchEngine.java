import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    // Индексированные слова
    Map<String, List<PageEntry> > indexedWords = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        // прочтите тут все pdf и сохраните нужные данные,
        // тк во время поиска сервер не должен уже читать файлы
        File[] listOfFiles = pdfsDir.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    //System.out.println(file.getName());
                    var doc = new PdfDocument(new PdfReader(file));
                    int pagesCount = doc.getNumberOfPages();
                    for (int i = 1; i <= pagesCount; i++) {
                        var page = doc.getPage(i);
                        var text = PdfTextExtractor.getTextFromPage(page);
                        var words = text.split("\\P{IsAlphabetic}+");
                        //System.out.println(String.join(" ", words));
                        // подсчёт частоты слов
                        Map<String, Integer> freqs = new HashMap<>(); // мапа, где ключом будет слово, а значением - частота
                        for (var word : words) { // перебираем слова
                            if (word.isEmpty()) {
                                continue;
                            }
                            word = word.toLowerCase();
                            freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                        }
                        // добавление в индекс
                        for(var entry : freqs.entrySet()) {
                            addWordToIndex(entry.getKey(), file.getName(), i, entry.getValue());
                        }
                    }
                }
            }
        }
        // отсортируем списки
        for(var entry : indexedWords.entrySet()) {
            Collections.sort(entry.getValue());
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        // тут реализуйте поиск по слову
        var word1 = word.toLowerCase();
        if(indexedWords.containsKey(word1)) {
            return indexedWords.get(word1);
        }
        return Collections.emptyList();
    }

    private void addWordToIndex(String word, String pdfName, int pageNumber, int count) {
        if(indexedWords.containsKey(word)) {
            var pages = indexedWords.get(word);
            pages.add(new PageEntry(pdfName, pageNumber, count));
        }
        else {
            indexedWords.put(word, new ArrayList<>());
            indexedWords.get(word).add(new PageEntry(pdfName, pageNumber, count));
        }
    }
}
