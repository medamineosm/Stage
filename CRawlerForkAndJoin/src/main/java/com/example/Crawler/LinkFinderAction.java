package com.example.Crawler;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.BodyTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.springframework.web.client.RestTemplate;

import static java.util.concurrent.ForkJoinTask.invokeAll;

/**
 * Created by OUASMINE Mohammed Amine on 09/05/2016.
 */
public class LinkFinderAction extends RecursiveAction {

    private String url;
    private LinkHandler cr;
    /**
     * Used for statistics
     */
    private static final long t0 = System.nanoTime();

    public LinkFinderAction(String url, LinkHandler cr) {
        this.url = url;
        this.cr = cr;
    }

    @Override
    public void compute() {
        RestTemplate restTemplate = new RestTemplate();
        if (!cr.visited(url)) {
            try {
                List<RecursiveAction> actions = new ArrayList<RecursiveAction>();
                URL uriLink = new URL(url);
                Parser parser = new Parser(uriLink.openConnection());

                NodeList list = parser.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));
                NodeList listBody = parser.extractAllNodesThatMatch(new NodeClassFilter(BodyTag.class));
                for (int i = 0; i < list.size(); i++) {
                    LinkTag extracted = (LinkTag) list.elementAt(i);

                    if (!extracted.extractLink().isEmpty()
                            && !cr.visited(extracted.extractLink())) {

                        actions.add(new LinkFinderAction(extracted.extractLink(), cr));
                    }
                }
                for (int i = 0; i < listBody.size(); i++) {
                    BodyTag extracted = (BodyTag) list.elementAt(i);

                    if (!extracted.getBody().isEmpty()) {

                        System.out.println("Je dois insert la !");
                    }
                }
                cr.addVisited(url);

                if (cr.size() == 1500) {
                    double time = (double)((System.nanoTime() - t0) / 1000000000.0);
                    System.out.println("Time for visit 1500 distinct links= " + time);
                }

                //invoke recursively
                invokeAll(actions);
            } catch (Exception e) {
                //ignore 404, unknown protocol or other server errors
            }
        }
    }
}
