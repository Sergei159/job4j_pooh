package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicService implements Service {
    private final  ConcurrentHashMap<String,
            ConcurrentLinkedQueue<String>> topic = new ConcurrentHashMap<>();
    private final  ConcurrentHashMap<String,
            ConcurrentLinkedQueue<String>> users = new ConcurrentHashMap<>();


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
        String param = req.getParam();
        String sourceName = req.getSourceName();
        String status = "200";
        topic.putIfAbsent(sourceName, new ConcurrentLinkedQueue<>());
        topic.get(sourceName).add(param);
        return  new Resp(param,  status);
    }

    private Resp get(Req req) {
        String id = req.getParam();
        String sourceName = req.getSourceName();
        String status = "200";
        if (users.size() == 0) {
            return new Resp("", "204");
        }
        topic.putIfAbsent(sourceName, new ConcurrentLinkedQueue<>());
        String text = topic.get(sourceName).poll();
        users.putIfAbsent(id, new ConcurrentLinkedQueue<>());
        if (text != null) {
            users.get(id).add(text);
        }
        return new Resp(text, status);
    }

}