package domain;

import domain.card.Card;
import domain.card.CardFactory;
import domain.user.Dealer;
import domain.user.Player;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

public class GameProcess {
    private Scanner sc = new Scanner(System.in);
    private String[] playerName;
    private int[] betting;
    private double[] win_or_lose;
    private List<Card> cards = new ArrayList<>();
    private List<Dealer> player = new ArrayList<>();    // player[0]에 딜러, 나머지는 플레이어
    private final int DEALER_INDEX = 0;
    private final int ZERO_BETTING = 0;
    private final int MAX_CARD_NUMBER = 52;
    private final int BLACKJACK = 21;
    private final double WIN = 1.5, DRAW = 1, LOSE = -1;

    public void Game() {
        inputName();
        inputBetting();
        cards = CardFactory.create();
        createPlayer();
        GameStart();
        check_First();
        actPlayer();
        checkDealerCard(player.get(DEALER_INDEX));
        getAllPlayerScore();
    }

    private void inputName() {
        System.out.println("게임에 참여할 사람의 이름을 입력하세요." +
                "(쉼표 기준으로 분리)");

        String input = sc.nextLine().trim();
        if (input.isEmpty()) {
            inputName();
            return;
        }
        playerName = input.split(",");
    }

    private void inputBetting() {
        betting = new int[playerName.length];

        for (int i = 0; i < playerName.length; i++) {
            while (betting[i] <= ZERO_BETTING) {
                System.out.println(playerName[i] + "의 배팅 금액은? (금액은 0원 이상이어야 합니다.)");
                betting[i] = sc.nextInt();
            }
        }
    }

    private void createPlayer(){
        player.add(new Dealer());
        for(int i = 0; i < playerName.length; i++){
            player.add(new Player(playerName[i],betting[i]));
        }
    }

    private void deal_OneCard(Dealer dealer){
        Random random = new Random();

        int cardChoose = random.nextInt(MAX_CARD_NUMBER);
        while(cards.get(cardChoose).isUsed()){
            cardChoose = random.nextInt(MAX_CARD_NUMBER);
        }
        cards.get(cardChoose).use();
        dealer.addCard(cards.get(cardChoose));
    }

    private void deal_TwoCard(Dealer dealer){
        int Two = 2;

        System.out.println(dealer.getName()+ "에게 두 장의 카드를 주었습니다.");
        for(int i = 0; i < Two; i++){
            deal_OneCard(dealer);
        }
    }

    private void GameStart(){
        for(int i = 0; i < player.size(); i++){
            deal_TwoCard(player.get(i));
            System.out.println(player.get(i));
        }
    }

    private void check_First(){
        win_or_lose = new double[player.size()];
        for(int i = 1; i < player.size(); i++){
            if(player.get(DEALER_INDEX).getCardSum() == BLACKJACK
                    && player.get(i).getCardSum() == BLACKJACK){
                win_or_lose[i] = DRAW;
            }else if(player.get(i).getCardSum() == BLACKJACK
                        && player.get(DEALER_INDEX).getCardSum() != BLACKJACK){
                win_or_lose[i] = WIN;
            }
        }
    }

    private void chooseDeal(Dealer player){
        char answer = 'y';
        while(answer == 'y' && player.getCardSum() < 21){
            System.out.println(player.getName() + "는 카드를 받으시겠습니까?"
                                + "(예는 y, 아니요는 n)");
            answer = sc.next().charAt(0);
            if(answer == 'n'){
                System.out.println(player);
                return;
            }else{
                deal_OneCard(player);
                System.out.println(player);
            }
        }
    }

    private void actPlayer(){
        for(int i = 1; i < player.size(); i++){
            chooseDeal(player.get(i));
            if(player.get(i).getCardSum() > 21){
                win_or_lose[i] = LOSE;
            }
        }
    }

    private void checkDealerCard(Dealer dealer){
        while(dealer.getCardSum() <= 16){
            System.out.println("\n딜러는 카드를 한 장 받았습니다.");
            deal_OneCard(dealer);
        }
    }

    private void getAllPlayerScore(){
        for(int i = 0; i < player.size(); i++){
            System.out.println(player.get(i) + "- 결과 :" + player.get(i).getCardSum());
        }
    }


}
