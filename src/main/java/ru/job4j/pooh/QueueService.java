package ru.job4j.pooh;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Отправитель посылает запрос на добавление данных с указанием очереди (weather) и значением параметра (temperature=18).
 *
 * Сообщение помещается в конец очереди. Если очереди нет в сервисе, то нужно создать новую и поместить в нее сообщение.
 *
 * Получатель посылает запрос на получение данных с указанием очереди. Сообщение забирается из начала очереди и удаляется.
 *
 * Если в очередь приходят несколько получателей, то они поочередно получают сообщения из очереди.
 *
 * Каждое сообщение в очереди может быть получено только одним получателем.
 */
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