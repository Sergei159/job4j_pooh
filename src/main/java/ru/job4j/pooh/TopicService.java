package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * суть работы см. в тесте.
 *
 * Отправитель посылает запрос на добавление данных с указанием топика (weather) и значением параметра (temperature=18).
 * Сообщение помещается в конец каждой индивидуальной очереди получателей. Если топика нет в сервисе, то данные игнорируются.
 *
 * Получатель посылает запрос на получение данных с указанием топика. Если топик отсутствует, то создается новый.
 * А если топик присутствует, то сообщение забирается из начала индивидуальной очереди получателя и удаляется.
 *
 * Когда получатель впервые получает данные из топика – для него создается индивидуальная пустая очередь.
 * Все последующие сообщения от отправителей с данными для этого топика помещаются в эту очередь тоже.
 *
 * Таким образом в режиме "topic" для каждого потребителя своя будет уникальная очередь с данными,
 * в отличие от режима "queue", где для все потребители получают данные из одной и той же очереди.
 *
 */

public class TopicService implements Service {
    private final ConcurrentHashMap<String,
            ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> topics
            = new ConcurrentHashMap<>();


    @Override
    public Resp process(Req req) {
        if ("POST".equals(req.httpRequestType())) {
            return post(req);
        } else if (("GET".equals(req.httpRequestType()))) {
           return get(req);
        } else {
            return new Resp("", "501");
        }
    }

    private Resp post(Req req) {
        String status = "200";
        String contextToPost = req.getParam();
        String sourceName = req.getSourceName();
        ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> topic
                = topics.get(sourceName);
        if (topic != null) {
            topic.forEach((key, value) -> value.add(contextToPost));
            return new Resp(contextToPost, status);
        } else {
            return new Resp("", "204");
        }
    }


    private Resp get(Req req) {
        String id = req.getParam();
        String sourceName = req.getSourceName();
        String status = "200";

        topics.putIfAbsent(sourceName, new ConcurrentHashMap<>());
        topics.get(sourceName).putIfAbsent(id, new ConcurrentLinkedQueue<>());
        String getString = topics.get(sourceName).get(id).poll();
        if (getString != null) {
            return new Resp(getString, status);
        } else {
            return new Resp("", "204");
        }
    }
}