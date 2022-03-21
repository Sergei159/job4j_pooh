package ru.job4j.pooh;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService implements Service {
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> queue
            = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {

        if ("POST".equals(req.httpRequestType())) {
            return post(req);
        } else if (("GET".equals(req.httpRequestType()))) {
            return get(req);
        } else {
            throw new IllegalArgumentException("Wrong query");
        }
    }

    private Resp post(Req req) {
        String text = req.getParam();
        String sourceName = req.getSourceName();
        String status = "200";
        queue.putIfAbsent(sourceName, new ConcurrentLinkedQueue<>());
        queue.get(sourceName).add(text);
        if ("".equals(text)) {
            status = "204";
        }
        return new Resp(text, status);
    }

    private Resp get(Req req) {
        String text = req.getParam();
        String sourceName = req.getSourceName();
        String status = "200";
        String textResult = queue.get(sourceName).poll();
        return new Resp(textResult, status);

    }
}