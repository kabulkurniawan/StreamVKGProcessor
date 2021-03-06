package sepses.streamVKG.stream;

//import it.polimi.yasper.core.stream.data.DataStreamImpl;
import org.apache.commons.io.IOUtils;
import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.*;

import it.polimi.yasper.core.stream.data.DataStreamImpl;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class TcpSocketStream extends DataStreamImpl implements Runnable  {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataStreamImpl<Graph> s;
    private String type;
    private int port;
    public TcpSocketStream(String stream_uri, int p) {
        super(stream_uri);
        this.port = p;

    }

    public void setWritable(DataStreamImpl<Graph> e) {
        this.s = e;
    }


    public TcpSocketStream(String stream_uri) {
        super(stream_uri);
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Listening at port: " + port);
            clientSocket = serverSocket.accept();
            System.out.println("New client connected");
           BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String line;
            ArrayList<String> trip = new ArrayList<>();

            while ((line = in.readLine()) != null) {
                //System.out.println(line);
                trip.add(line);
                if(trip.size()>=4) {
//                    System.out.println(trip);
                    Model dataModel = ModelFactory.createDefaultModel();
                    dataModel.read(IOUtils.toInputStream(trip.get(0)+" "+trip.get(1)+" "+trip.get(2), "UTF-8"), null, "N3");
                    this.s.put(dataModel.getGraph(), System.currentTimeMillis());
                    trip.clear();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
