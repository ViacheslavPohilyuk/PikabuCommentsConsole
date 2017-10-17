import java.io.IOException;
import java.util.Scanner;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by mac on 13.10.17.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        String nickname = "";
        System.out.println("Введите имя пользователя:");
        Scanner sc = new Scanner(System.in);
        nickname = sc.next().trim();

        Document doc = null;
        try {
            long beginTime = System.currentTimeMillis();
            doc = Jsoup.connect("https://pikabu.ru/profile/" + nickname).get();

            // Checking the existence of a user's profile
            String title = doc.title();
            if (title.equals("404. Страница не найдена")) {
                System.out.println("Такого пользователя не существует!");
            }

            // Recognizing the gender of a user
            String gender = "";
            Elements genderImgList = doc.select("img[title^=пол:]");
            if (!genderImgList.isEmpty()) {
                String elemImgGenderValue = genderImgList.get(0).attr("title");
                int indexOfSpace = elemImgGenderValue.indexOf(" ");
                gender = elemImgGenderValue.substring(indexOfSpace + 1);
            }

            // Find out the count of user's comments
            Element elemCommentsCount = doc.select("span:contains(комментариев) + span").get(0);
            String commentsCount = elemCommentsCount.html();
            if (commentsCount.equals("0")) {
                System.out.println("У пользователя нет комментариев!");
                System.exit(0);
            }

            String googleQueryPart = "";
            if (gender.equals("мужской")) {
                googleQueryPart = "отправил";
            } else if (gender.equals("женский")) {
                googleQueryPart = "отправила";
            } else {
                googleQueryPart = "отправлено";
            }
            String findUserCommentsQuery = "site:pikabu.ru+\"" + nickname + "+" + googleQueryPart + "\"";
            String googleFindUserCommentsQuery = "https://www.google.com/search?q=" + findUserCommentsQuery;
            Document docStories = Jsoup.connect(googleFindUserCommentsQuery).get();
            Elements userStories = docStories.select("a[href^=\"http://pikabu.ru/story/\"], [href^=\"https://pikabu.ru/story/\"]");
            for (Element story : userStories) {
                // System.out.println(elCom.attr("href"));
                String linkPost = story.attr("href");
                Document postHtml = Jsoup.connect(linkPost).get();

                // Title of a post
                Element storyTitle = postHtml.select("div.story__header-title a").get(0);
                System.out.println(storyTitle.text());
                System.out.println("Ссылка на пост: " + storyTitle.attr("href"));

                Elements comments = postHtml.select("div.b-comment__body:contains(" + nickname + ")");
                for (Element comm : comments) {
                    Element header = comm.getElementsByClass("b-comment__header").get(0);

                    // Rating of a comment
                    String rating = header.getElementsByClass("b-comment__rating-count").get(0).text();
                    System.out.println("\tРейтинг: " + rating);

                    // Getting and displaying date and time when a comment has been posted
                    String dateTime = header.getElementsByClass("b-comment__time").get(0).text();
                    System.out.println("\tДата: " + dateTime);

                    // Content of a comment
                    System.out.println("\tСодержание: ");
                    String content = comm.getElementsByClass("b-comment__content").get(0).text();
                    System.out.println("\t" + content);

                    // Link to a comment in a post
                    String commentLink = header.getElementsByClass("b-comment__tools").get(0)
                            .getElementsByTag("a").get(0)
                            .attr("href");
                    System.out.println("\tСсылка на комментарий: " + commentLink + "\n");
                }
            }

            double runTimeSeconds = (System.currentTimeMillis() - beginTime) / 1000D;
            System.out.println("\nRun time: " + runTimeSeconds + " seconds");
        } catch (HttpStatusException ex) {
            System.err.println("Недопустимые символы в имени пользователя!");
        }
    }
}
