import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchResult {
    private final List<PageEntry> pages;

    public SearchResult() {
        pages = new ArrayList<>();
    }

    public SearchResult(List<PageEntry> pages) {
        this.pages = pages;
    }

    public void addPages(List<PageEntry> newPages) {
        for(var p : newPages) {
            addPage(p);
        }
    }

    void addPage(PageEntry page) {
        for (var p : pages) {
            if(Objects.equals(page.getPdfName(), p.getPdfName()) && page.getPage() == p.getPage()) {
                p.setCount(p.getCount() + page.getCount());
                return;
            }
        }
        pages.add(page);
    }

    public List<PageEntry> getPages() {
        return pages;
    }

    public String toJson() {
        if(pages.isEmpty())
            return "Ничего не найдено";
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(pages);
    }
}
