import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.*;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    // Индексированные слова
    private final Map<String, List<PageEntry> > indexedWords = new HashMap<>();
    // Список стоп слов, которые не нужно учитывать при поиске
    private final Set<String> stopWords = new HashSet<>();

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
        // Загрузка стоп слов
        String stopWordsFile = "stop-ru.txt";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(stopWordsFile)));
        String line;
        while ((line = br.readLine()) != null) {
            stopWords.add(line.toLowerCase());
        }
    }

    @Override
    public List<PageEntry> search(String text) {
        // Разбить текст на слова.
        // Пройти по всем словам и попробовать найти.
        // Если слово в стоп листе, то пропустить.
        // В результате будет список страниц. У каждой найденной страницы количество слов будет равно
        // сумме всех слов, найденных на этой странице.
        var words = text.split("\\P{IsAlphabetic}+");// Получаем слова из запроса
        //System.out.println(Arrays.asList(words));
        SearchResult searchResult = new SearchResult();//Создаем объект результата поиска
        // Запускаем цикл по каждому слову из запроса
        for(var word : words) {
            var word1 = word.toLowerCase();//Преобразуем к нижнему регистру
            if(stopWords.contains(word1))//Если слово в стоп листе,
                continue;//переходим к следующему слову
            if (indexedWords.containsKey(word1)) {//Если слово есть в индексе,
                //получаем страницы из индекса, где это слово есть и добавляем в результат
                searchResult.addPages(indexedWords.get(word1));
            }
        }
        searchResult.sort();//Сортируем результат
        return searchResult.getPages();//Возвращаем результат
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
