package ru.job4j.pooh;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService implements Service {
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> queue
            = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        String text = req.getParam();
        String sourceName = req.getSourceName();
        String status = "200";

        if ("POST".equals(req.httpRequestType())) {
            queue.putIfAbsent(sourceName, new ConcurrentLinkedQueue<>());
            queue.get(sourceName).add(text);
            if ("".equals(text)) {
                status = "204";
            }
            return new Resp(text, status);

        } else if (("GET".equals(req.httpRequestType()))) {
            String textResult = queue.get(sourceName).poll();
            return new Resp(textResult, status);
        } else {
            throw new IllegalArgumentException("Wrong query");
        }
    }
}