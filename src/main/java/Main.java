import java.io.IOException;
import java.util.Scanner;

import com.sun.xml.internal.xsom.impl.Ref;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;

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
                System.out.println("User's gender: " + gender);
            }

            // Find out the count of user's comments
            Element elemCommentsCount = doc.select("span:contains(комментариев) + span").get(0);
            String commentsCount = elemCommentsCount.html();
            if (commentsCount.equals("0")) {
                System.out.println("У пользователя нет комментариев!");
                System.exit(0);
            }
            System.out.println("User's comments count: " + commentsCount);

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
            System.out.println(googleFindUserCommentsQuery);

            Document docComms = Jsoup.connect(googleFindUserCommentsQuery).get();
            // System.out.println(docComms.body());

            Elements elemsComments = docComms.select("a[href^=\"http://pikabu.ru/story/\"], [href^=\"https://pikabu.ru/story/\"]");
            for (Element elCom : elemsComments) {
                System.out.println(elCom.attr("href"));
            }

            String linkPost = elemsComments.get(0).attr("href");
            Document postHtml = Jsoup.connect(linkPost).get();


            Elements comments = postHtml.select("div.b-comment__body:contains(" + nickname + ")");
            //Element comment = comments.get(0);

            for (Element comm : comments) {
                System.out.println(comm.getElementsByClass("b-comment__content").get(0).text());
            }

            //System.out.println(postHtml.select("div.b-comment span:contains(" + nickname + ")"));

            // div.b-comment span:contains(Yukiri)

            double runTimeSeconds = (System.currentTimeMillis() - beginTime) / 1000D;
            System.out.println("Run time: " + runTimeSeconds + " seconds");
        } catch (HttpStatusException ex) {
            System.err.println("Недопустимые символы в имени пользователя!");
        }
    }
}
