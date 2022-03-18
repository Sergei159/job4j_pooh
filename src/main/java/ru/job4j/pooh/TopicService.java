package ru.job4j.pooh;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicService implements Service {
    private final  ConcurrentHashMap<String,
            ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> topic
            = new ConcurrentHashMap<>();

    private NameOfSubscriber nameOfSubscriber;

    private static class NameOfSubscriber {
        private String name;


        public NameOfSubscriber(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Override
    public Resp process(Req req) {
        String param = req.getParam();
        String sourceName = req.getSourceName();
        String status = "200";
        if ("POST".equals(req.httpRequestType())) {
            topic.get(sourceName).get(nameOfSubscriber.getName()).add(param);
            return new Resp("", status);

        } else if (("GET".equals(req.httpRequestType()))) {
            boolean isNotAbsent = topic.putIfAbsent(
                    sourceName,
                    new ConcurrentHashMap<>()) == null;
            if (isNotAbsent) {
                topic.get(sourceName).put(param, new ConcurrentLinkedQueue<>());
            }
            nameOfSubscriber = new NameOfSubscriber(param);
            String textResult = topic.get(sourceName).get(param).poll();
            if (textResult == null) {
                textResult = "";
                status = "204";
            }
            return new Resp(textResult, status);
        } else {
            throw new IllegalArgumentException("Wrong query");
        }
    }
}