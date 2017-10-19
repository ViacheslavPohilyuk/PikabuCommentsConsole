import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by mac on 19.10.17.
 */
public class UserCommentsRecognizer {
    private String username; // user's name on the website Pikabu.ru

    UserCommentsRecognizer(String username) {
        this.username = username;
    }

    /**
     * Approach of this method is checking user on existence on the Pikabu.ru
     * and recognizing one's gender and comments count.
     * And finally forms a google query to find user's comments on the Pikabu.
     *
     * @return query to find out user's comments for search engine of Google
     */
    public String checkUserProfileInfo() {
        Document doc = null;
        String googleFindUserCommentsQuery = "";
        try {
            doc = Jsoup.connect("https://pikabu.ru/profile/" + username).get();

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
            switch (gender) {
                case "мужской": {
                    googleQueryPart = "отправил";
                    break;
                }
                case "женский": {
                    googleQueryPart = "отправила";
                    break;
                }
                default:
                    googleQueryPart = "отправлено";
            }
            googleFindUserCommentsQuery =
                    "https://www.google.com/search?q=site:pikabu.ru+\""
                            + username + "+"
                            + googleQueryPart + "\"";

        } catch (HttpStatusException ex) {
            System.err.println("Недопустимые символы в имени пользователя!");
        } catch (IOException ioe) {
            System.err.println("Ошибка подключния к сайту Pikabu.ru!");
        }
        return googleFindUserCommentsQuery;
    }

    /**
     * This method sends a query to Google search engine to find Pikabu stories
     * where the user writes his comments and traverses on the web-pages of these
     * ones to retrieve comments of a current user.
     *
     * @param findCommentsQuery google query to find user's comments
     */
    public void recognizeUserComments(String findCommentsQuery) {
        //long beginTime = System.currentTimeMillis();

        try {
            Boolean isNext;
            int pageStart = 0;
            String startGooglePage = "";
            do {
                isNext = false;
                Document docStories = Jsoup.connect(findCommentsQuery + startGooglePage).get();
                Elements userStories = docStories.select("a[href^=\"http://pikabu.ru/story/\"], [href^=\"https://pikabu.ru/story/\"]");
                for (Element story : userStories) {
                    String linkPost = story.attr("href");
                    Document postHtml = Jsoup.connect(linkPost).get();

                    // Title of a post
                    Element storyTitle = postHtml.select("div.story__header-title a").get(0);
                    System.out.println(storyTitle.text());
                    System.out.println("Ссылка на пост: " + storyTitle.attr("href"));

                    Elements comments = postHtml.select("div.b-comment__body:contains(" + username + ")");
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
                System.out.println("Показать следующие 10 постов? (y/n)");
                Scanner sc = new Scanner(System.in);
                String answer = sc.next();
                if (answer.equals("y")) {
                    isNext = true;
                    pageStart += 10;
                    startGooglePage = "&start=" + pageStart;
                    System.out.println("startGooglePage: " + startGooglePage);
                    System.out.println("findCommentsQuery + startGooglePage :" + findCommentsQuery + startGooglePage);
                }
            }
            while (isNext);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //double runTimeSeconds = (System.currentTimeMillis() - beginTime) / 1000D;
        //System.out.println("Run time: " + runTimeSeconds);
    }
}
