package pool;

import java.net.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.lang.Exception;

class EchoServerTPool {

    /* Démarrage et délégation des connexions entrantes */
    public void demarrer(int port) throws Exception {
        ServerSocket ssocket; // socket d'écoute utilisée par le serveur
        Socket csocket;

        System.out.println("Lancement du serveur sur le port " + port);
        try
        {
            ssocket = new ServerSocket(port);
            ssocket.setReuseAddress(true); /* rend le port réutilisable rapidement */
            ExecutorService pool = Executors.newWorkStealingPool();

            while (true)
            {
                //(new Handler(ssocket.accept())).run();
                csocket = ssocket.accept();
                Handler ch = new Handler(csocket);
                pool.execute(ch);
            }
        } catch (IOException ex)
        {
            System.out.println("Arrêt anormal du serveur."+ ex);
            return;
        }
    }

    public static void main(String[] args) {
        int argc = args.length;
        EchoServerTPool serveur;

        /* Traitement des arguments */
        if (argc == 1)
        {
            try
            {
                serveur = new EchoServerTPool();
                serveur.demarrer(Integer.parseInt(args[0]));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        } else
        {
            System.out.println("Usage: java EchoServerTPool port");
        }
        return;
    }

    /*
       echo des messages reçus (le tout via la socket).
       NB classe Runnable : le code exécuté est défini dans la
       méthode run().
    */
    class Handler implements Runnable {

        Socket socket;
        PrintWriter out;
        BufferedReader in;
        InetAddress hote;
        int port;

        Handler(Socket socket) throws IOException
        {
            this.socket = socket;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            hote = socket.getInetAddress();
            port = socket.getPort();
        }

        public void run()
        {
            String tampon;
            long compteur = 0;

            try
            {
                /* envoi du message d'accueil */
                out.println("Bonjour " + hote + "! (vous utilisez le port " + port + ")");

                do
                {
                    /* Faire echo et logguer */
                    tampon = in.readLine();
                    if (tampon != null)
                    {
                        compteur++;
                        /* log */
                        System.err.println("[" + hote + ":" + port + "]: " + compteur + ":" + tampon);
                        /* echo vers le client */
                        out.println("> " + tampon);
                    } else
                    {
                        break;
                    }
                } while (true);

                /* le correspondant a quitté */
                if(!socket.isClosed())
                {
                    in.close();
                    out.println("Au revoir...");
                    out.close();
                    socket.close();

                    System.err.println("[" + hote + ":" + port + "]: Terminé...");
                }
            } catch (Exception e)

            {
                e.printStackTrace();
            }
        }
    }
}