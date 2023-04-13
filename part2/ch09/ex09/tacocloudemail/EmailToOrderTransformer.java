package tacocloud.tacocloudemail;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.integration.mail.transformer.AbstractMailMessageTransformer;
import org.springframework.integration.support.AbstractIntegrationMessageBuilder;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class EmailToOrderTransformer
        extends AbstractMailMessageTransformer<Order>{

    private static final String SUBJECT_KEYWORDS = "TACO ORDER";


    @Override
    protected AbstractIntegrationMessageBuilder<Order> doTransform(Message mailMessage) throws Exception {
        Order tacoOrder = processPayload(mailMessage);
        return MessageBuilder.withPayload(tacoOrder);
    }

    private Order processPayload(Message mailMessage){
        try{
            String subject = mailMessage.getSubject();

            if(subject.toUpperCase().contains(SUBJECT_KEYWORDS)){
                String email =
                        ((InternetAddress) mailMessage.getFrom()[0]).getAddress(); // ??
                String content = mailMessage.getContent().toString();

                return parseEmailToOrder(email, content);
            }
        } catch(MessagingException e){
        } catch(IOException e){}
        return null;
    }


    /**
     * What is \\ r ?\\ n for in Java?
     * Adding Newline Characters in a String
     *
     * trim = 문자열 공백 삭제
     */
    private Order parseEmailToOrder(String email, String content){
        Order order = new Order(email);
        String[] lines = content.split("\\r?\\n");

        for(String line : lines){
            if(line.trim().length() > 0 && line.contains(":")){ // ?
                String[] lineSplit = line.split(":");
                String tacoName = lineSplit[0].trim();
                String ingredients = lineSplit[1].trim();
                String[] ingredientsSplit = ingredients.split(",");
                List<String> ingredientsCodes = new ArrayList<>();
                for(String ingredientName : ingredientsSplit){
                    String code = lookupIngredientCode(ingredientName.trim());
                    if(code != null){
                        ingredientsCodes.add(code);
                    }
                }
                Taco taco = new Taco(tacoName);
                taco.setIngredients(ingredientsCodes);
                order.addTaco(taco);
            }
        }
        return order;
    }


    private String lookupIngredientCode(String ingredientName){
        for(Ingredient ingredient : ALL_INGREDIENTS){
            String ucIngredientName = ingredientName.toUpperCase();
            if(LevenshteinDistance.getDefaultInstance().apply(ucIngredientName, ingredient.getName()) < 3 ||
            ingredient.getName().contains(ucIngredientName)){
                return ingredient.getCode();
            }
        }
        return null;
    }

    private static Ingredient[] ALL_INGREDIENTS = new Ingredient[] {
            new Ingredient("FLTO", "FLOUR TORTILLA"),
            new Ingredient("COTO", "CORN TORTILLA"),
            new Ingredient("GRBF", "GROUND BEEF"),
            new Ingredient("CARN", "CARNITAS"),
            new Ingredient("TMTO", "TOMATOES"),
            new Ingredient("LETC", "LETTUCE"),
            new Ingredient("CHED", "CHEDDAR"),
            new Ingredient("JACK", "MONTERREY JACK"),
            new Ingredient("SLSA", "SALSA"),
            new Ingredient("SRCR", "SOUR CREAM")
    };

}
