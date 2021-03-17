package pool;

import java.net.Socket;
import java.net.InetAddress;
import java.io.PrintWriter;

public class Stress1
{
    public static void main(String[] args)
    {
        int nbCS=0;
        Socket sc;
        PrintWriter out;

        try
        {
            if(args.length != 1 || Integer.parseInt(args[0])<=0)
            {
                System.out.println("Passez en argument le nombre de clients stressants (>0).");
                return;
            }
            else
            {
                nbCS = Integer.parseInt(args[0]);
                System.out.println("Il y a "+nbCS+" clients stressants");
            }

            for(int i=1; i<nbCS+1; i++)
            {
                sc = new Socket(InetAddress.getByName("localhost"), 2222);
                out = new PrintWriter(sc.getOutputStream());
                String msg;

                msg = "client stress1 n°"+i;
                out.println(msg);
                out.flush();
                //sc.close();
            }
        }catch(Exception e)
        {
            System.out.println("Une erreur s'est produite");
            e.printStackTrace();
        }
    }
}