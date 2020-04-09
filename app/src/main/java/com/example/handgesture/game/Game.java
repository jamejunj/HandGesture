import java.util.Random;

class Game{
    String[] items = {"Rock","Paper","Scisor"};
    private Integer status = null;
    Random gen = new Random();
    public int Play(int player){
        int robot = gen.nextInt(2);
        if (robot==player+1 || player==2 && robot==0){
            status = -1;
        }else if (robot==player-1 || player==0 && robot==3){
            status = 1;
        }else{
            status = 0;
        }
        return status;
    }

    @Override
    public String toString(){
        String text;
        if (status==-1){
            text = "LOSE";
        }else if (status==1){
            text = "WIN";
        }else if (status==0){
            text = "DRAW";
        }else{
            text = "N/A";
        }
        return text;
    }
}