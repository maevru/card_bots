import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Speelwijze
 * De bedoeling is om te voorkomen dat men strafpunten krijgt toegespeeld. De strafpunten bij hartenjagen zijn alle harten (elk één strafpunt) en de schoppenvrouw (dertien strafpunten).
 *
 * De speler die na het uitdelen en doorgeven van de kaarten klaveren twee heeft moet voor de eerste slag uitkomen. Met de klok mee moeten de overige spelers een kaart bijleggen. Men moet kleur bekennen.
 *
 * Als alle slagen gespeeld zijn is er een ronde gespeeld en tellen alle spelers hun strafpunten op. In totaal zijn er per ronde 26 strafpunten te verdelen. Echter, als een speler erin slaagt alle 26 punten te verzamelen (in het Engels “Shooting the Moon” genoemd, in het Nederlands “een doorslag halen”, "een pit halen" of "kapot spelen") krijgen zijn drie tegenstanders 26 strafpunten en hij of zij zelf niets.
 *
 * Gentleman’s rules
 *
 * Bij de eerste slag mogen nog geen strafkaarten worden gespeeld.
 *
 * Een speler mag geen harten spelen voordat een andere speler bij een eerdere slag een harten heeft gespeeld
 * .
 * De speler die schoppenvrouw heeft, moet deze kaart bij de eerste de beste gelegenheid spelen.
 *
 * In sommige gevallen wordt vooraf afgesproken dat klaverentwee en schoppenvrouw niet mogen worden doorgegeven aan het begin van een ronde.
 */

public class RandomPlayer implements BlackQueenPlayer{

    private final String name;
    private ArrayList<String> cards = new ArrayList<>();
    public static final Random RNG = new Random();

    public RandomPlayer(String name){
        this.name = name;
    }

    public void setCards(ArrayList<String> cards){
        this.cards = cards;
        Collections.sort(cards);
    }

    public ArrayList<String> getCards(){
        return cards;
    }

    public String getName(){
        return name;
    }

    public boolean hasCard(String card){
        return cards.contains(card);
    }

    /**
     * @param history string format: Y/N (wel of geen strafpunten gevallen) naam speler - kaart
     * @param round de gespeelde kaarten deze ronde
     * @return gespeelde kaart
     */
    public String playCard(String history, String round){
        if(round.equals("")){
            if(cards.contains("C1")){
                cards.remove("C1");
                return "C1";
            } else if(history.charAt(0) == 'Y'){
                String card = cards.get(RNG.nextInt(cards.size()));
                cards.remove(card);
                return card;
            } else {
                ArrayList<String> notHeart = new ArrayList<>();
                for(String card : cards){
                    if(card.charAt(0) != 'H'){
                        notHeart.add(card);
                    }
                }
                String card;
                if(notHeart.isEmpty()){
                    card = cards.get(RNG.nextInt(cards.size()));
                } else {
                    card = notHeart.get(RNG.nextInt(notHeart.size()));
                }
                cards.remove(card);
                return card;
            }
        } else {
            String[] splitRound = round.split(" ");
            char cartType = splitRound[1].charAt(0);

            ArrayList<String> type = new ArrayList<>();
            for(String card : cards){
                if(card.charAt(0) == cartType){
                    type.add(card);
                }
            }

            if(!type.isEmpty()){
                String card = type.get(RNG.nextInt(type.size()));
                cards.remove(card);
                return card;
            } else if(cards.contains("S11")){
                cards.remove("S11");
                return "S11";
            } else {
                String card = cards.get(RNG.nextInt(cards.size()));
                cards.remove(card);
                return card;
            }
        }
    }
}
