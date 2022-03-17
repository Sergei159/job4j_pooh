package ru.job4j.pooh;

/**
 *Req - класс, служит для парсинга входящего запроса.
 *
 * httpRequestType - GET или POST. Он указывает на тип запроса.
 *
 * poohMode - указывает на режим работы: queue или topic.
 *
 * sourceName - имя очереди или топика.
 *
 * param - содержимое запроса.
 */
public class Req {

    private final String httpRequestType;
    private final String poohMode;
    private final String sourceName;
    private final String param;

    public Req(String httpRequestType, String poohMode, String sourceName, String param) {
        this.httpRequestType = httpRequestType;
        this.poohMode = poohMode;
        this.sourceName = sourceName;
        this.param = param;
    }

    /** Метод парсит данные из входящего запроса
     * Запрос может быть POST или GET:
     *
     *POST /topic/weather -d "temperature=18"
     * где topic(Может быть queue) - режим poohMode
     * weather - имя
     * "temperature=18" - параметр поста (находится в восьмой строке запроса)
     *
     *
     * GET /topic/weather/1
     * где 1 - ID получателя, если пусто, значит метод должен вернуть
     * параметром пустую строку
     */
    public static Req of(String content) {
        String[] contentStings = content.split(System.lineSeparator());
        String[] firstStringContent = contentStings[0].split(" ");
        String[] data = firstStringContent[1].split("/");
        if (data.length != 4) {
            data = new String[] {data[0], data[1], data[2], ""};
        }

        if (("POST").equals(firstStringContent[0])) {
            return new Req("POST", data[1], data[2], contentStings[7]);
        } else if (("GET").equals(firstStringContent[0])) {
            return new Req("GET", data[1], data[2], data[3]);
        } else  {
            throw new IllegalArgumentException("Wrong query");
        }
    }

    public String httpRequestType() {
        return httpRequestType;
    }

    public String getPoohMode() {
        return poohMode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getParam() {
        return param;
    }
}