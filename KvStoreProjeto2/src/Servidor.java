
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;



public class Servidor {
	
	public static void main(String[] args) throws Exception {
		//Iniciando classe externa
		Servidor servidor = new Servidor();
		Scanner scan = new Scanner(System.in);
		
		//Variaveis
		String ipServ = null, ipLider = null;
		int portaServ,portaLider;
		
		//inicializando a hashMap pois ira trabalhar com threads
		Map<String,Mensagem> map = new HashMap<String,Mensagem>();
		
		
		//ListaServidorSeguidores 
        ArrayList<Integer> portasSeguidores = new ArrayList<>();
        
		
		//Ira pegar as informcao do servidor e da porta do lider
		System.out.println("Digite o ip do Servidor:");
		ipServ = scan.nextLine();
		System.out.println("Digite a porta do Servidor:");
		portaServ = scan.nextInt(); scan.nextLine();
		System.out.println("Digite o ip do Lider:");
		ipLider = scan.nextLine();
		System.out.println("Digite a porta do lider:");
		portaLider = scan.nextInt(); scan.nextLine();
		
		
		if(portaLider == 10098) {
			portasSeguidores.add(10099);
			portasSeguidores.add(10097);
		}else if (portaLider == 10097) {

			portasSeguidores.add(10099);
			portasSeguidores.add(10098);
		}else if(portaLider == 10099) {

			portasSeguidores.add(10098);
			portasSeguidores.add(10097);
		}
		
		
		
		try (
		//Inicializa o meu server com sua devida porta e fica esperando requisicoes 
		ServerSocket serverSocket = new ServerSocket(portaServ)) {
			while(true) {
				//Ira tratar por thread a solicitacao dos clientes para ficar livre para novas requisicoes
				Socket no = serverSocket.accept();
				ThreadAtendimento thread = servidor.new ThreadAtendimento(no,map,portaServ,portaLider,ipServ,portasSeguidores);
				thread.start();
			}
		}
	}



	
	
	
//Classe feita para tomada de dicesao do servidor e atendimento	
public class ThreadAtendimento extends Thread {
		
		private Socket no;
		private Map<String, Mensagem> map;
		private int portaServ,portaLider;
		private String ipServ;
		private ArrayList<Integer> portas;
		
		//random para gerar um timeStamp aleatorio
		Random gerador = new Random();
		
		public ThreadAtendimento(Socket node,Map map,int portaServ,int portaLider,String ipServ, ArrayList porta) {
			no = node;
			this.map = map;
			this.portaServ = portaServ;
			this.portaLider = portaLider;
			this.ipServ = ipServ;
			this.portas = porta;
		}
		
	
		public void run() {
			try {
				
				
				//Ira definir os objetos de saida e entrada a serem enviados por tcp
				ObjectOutputStream objetoOut = new ObjectOutputStream(no.getOutputStream());
                ObjectInputStream objetoIn = new ObjectInputStream(no.getInputStream());
                
                
                //Mensagem recebida pela requisicao
                Mensagem mensagemRecebida = (Mensagem) objetoIn.readObject();
                
                //Determinara a acao a ser tomada pelo servidor
                String acao = mensagemRecebida.getAcao();
                
                
                //Acoes a serem tomada pelo servidor caso for a requisicao PUT
                if (acao.equals("PUT")) {
                	
                	//QUANDO O SERVIDOR E O LIDER
                	if(portaServ == portaLider) {
                		
                		//Definindo a porta e IP que irao realizar o adicionamento
                		mensagemRecebida.setIpServidor(ipServ);
                		mensagemRecebida.setPortaServ(portaServ);
                		
                		//Print na Console
                		System.out.println("Cliente " + mensagemRecebida.getIpCliente() + ":" + mensagemRecebida.getPortaCliente() + " PUT Key:[" + mensagemRecebida.getKey() + "] value:[" + mensagemRecebida.getValue()+"]");
                		
                		
                		//Validando se a key ja existe
                		if(map.get(mensagemRecebida.getKey())!= null) {
                			Mensagem aux = (Mensagem) map.get(mensagemRecebida.getKey());
                			aux.setValue(mensagemRecebida.getValue());
                			aux.setTimeStamp(gerador.nextInt(30));
                			mensagemRecebida.setTimeStamp(aux.getTimeStamp());
                		} else {
                			//GERA UM TIMESTAMP ALEATORIO
                    		mensagemRecebida.setTimeStamp(gerador.nextInt(30));
                    		//COLOCA NA TABELA HASH DO LIDER
                    		map.put(mensagemRecebida.getKey(),mensagemRecebida );
                		}
                		
                		             		                		
                		
                		//IRA FAZER AGORA O REPLICATION
                		mensagemRecebida.setAcao("REPLICATION");
                		
                		
                		//Inicializando dois sockets para envio aos servidores
						Socket seguidor1 = new Socket("127.0.0.1", portas.get(0));
						Socket seguidor2 = new Socket("127.0.0.1", portas.get(1));
                		
                		
						
						//Envio servidor 1
						ObjectOutputStream outSeg1 = new ObjectOutputStream(seguidor1.getOutputStream());
		                ObjectInputStream inSeg1 = new ObjectInputStream(seguidor1.getInputStream());
		                
		                //Envio servidor 2
		                ObjectOutputStream outSeg2 = new ObjectOutputStream(seguidor2.getOutputStream());
		                ObjectInputStream inSeg2 = new ObjectInputStream(seguidor2.getInputStream());
                		
		            
		                
		                //Replicando a ambos os servidores
		                outSeg1.writeObject(mensagemRecebida);
		                outSeg2.writeObject(mensagemRecebida);
                		
                		
		                //Recebendo 
		                Mensagem mensagemSeg1 = (Mensagem) inSeg1.readObject();
		                Mensagem mensagemSeg2 = (Mensagem) inSeg2.readObject();
		                
		                System.out.println("Porta:" + mensagemSeg1.getPortaServ() + " Resposta:" + mensagemSeg1.getM());
		                System.out.println("Porta:" + mensagemSeg2.getPortaServ() + " Resposta:" + mensagemSeg2.getM());
		                seguidor1.close();
		                seguidor2.close();
		                
		                System.out.println("Enviando PUT_OK ao Cliente "+ mensagemRecebida.getIpCliente() + ":" + mensagemRecebida.getPortaCliente() + " da Key:[" + mensagemRecebida.getKey() + "] ts[" + mensagemRecebida.getTimeStamp() +"]");
		                //Envia mensagem pro CLIENTE
		                objetoOut.writeObject(mensagemRecebida);
		                System.out.println();
                		
                	}
	                //QUANDO O SERVIDOR NAO E O LIDER
                	else {
	                	
	                	
	                	//Cria uma conexão com o servidor
						Socket lider = new Socket("127.0.0.1", portaLider);
						
	
						//Ira definir os objetos de saida e entrada a serem enviados ao ServidorPrincipal
						ObjectOutputStream outLider = new ObjectOutputStream(lider.getOutputStream());
		                ObjectInputStream inLider = new ObjectInputStream(lider.getInputStream());
		                
		                //Para o Servidor principal atuar
		                mensagemRecebida.setAcao("PUT");
						
		                System.out.println("Encaminhado PUT Key:[" + mensagemRecebida.getKey() +"] value:[" + mensagemRecebida.getValue() + "]" );
		                System.out.println();
						//Classe a ser enviada
						outLider.writeObject(mensagemRecebida);
						
						//Classe a ser recebida;
						Mensagem mensagemRecebidaLider = (Mensagem) inLider.readObject();
					
						
						
						//Fechamento do canal
						lider.close();
						
						//ENVIA MENSAGEM PARA O CLIENTE
						objetoOut.writeObject(mensagemRecebidaLider);
                		
                	}
                	
                	
                }
                
                //REQUISICAO GET
                else if (acao.equals("GET")) {
                	
                	//timeStampAux
                	int timeStampAux = mensagemRecebida.getTimeStamp();
                	
                	if(map.get(mensagemRecebida.getKey())!= null) {
                		Mensagem aux = (Mensagem) map.get(mensagemRecebida.getKey());
                		
                		if (aux.getTimeStamp() >= mensagemRecebida.getTimeStamp()) {
                			mensagemRecebida.setValue(aux.getValue());
                			
                			
                		}else {
                			String aux2 = "TRY_OTHER_SERVER_OR_LATER";
                			mensagemRecebida.setValue(aux2);;
                			
                		}
                		mensagemRecebida.setTimeStamp(aux.getTimeStamp());
                		
            		} else {
            			mensagemRecebida.setValue(null);
            			mensagemRecebida.setTimeStamp(0);
            		}   
                	mensagemRecebida.setIpServidor(ipServ);
                	mensagemRecebida.setPortaServ(portaServ);
                	
                	//Imprimir mensagem na console
            		System.out.println("Cliente " + mensagemRecebida.getIpCliente() + ":" + mensagemRecebida.getPortaCliente() + " GET:[" + mensagemRecebida.getKey() + "] ts:[" +timeStampAux +"]");
            		System.out.println("Meu ts e " + mensagemRecebida.getTimeStamp() + ",portanto devolvendo [" + mensagemRecebida.getValue() +"]" );
                	System.out.println();
                	//ENVIA MENSAGEM PARA O CLIENTE
                	objetoOut.writeObject(mensagemRecebida);
                	
                }
                
                
                //Requisicao do servidor de Replication
                else if (acao.equals("REPLICATION")) {
                	
                	//Validando se a key ja existe
            		if(map.get(mensagemRecebida.getKey())!= null) {
            			Mensagem aux = (Mensagem) map.get(mensagemRecebida.getKey());
            			aux.setValue(mensagemRecebida.getValue());
            			aux.setTimeStamp(gerador.nextInt(30));
            			mensagemRecebida.setTimeStamp(aux.getTimeStamp());
            		} else {
            			//GERA UM TIMESTAMP ALEATORIO
                		mensagemRecebida.setTimeStamp(gerador.nextInt(30));
                		//COLOCA NA TABELA HASH DO LIDER
                		map.put(mensagemRecebida.getKey(),mensagemRecebida);
                		
            		}   
            		 System.out.println("REPLICATION Key:[" + mensagemRecebida.getKey() +"] value:[" + mensagemRecebida.getValue() + "] ts:["+ mensagemRecebida.getTimeStamp()+"]" );
            		 System.out.println();
            		 
            		 mensagemRecebida.setM("REPLICATION_OK");
            		 mensagemRecebida.setPortaServ(portaServ);
                	//ENVIA MENSAGEM PARA O CLIENTE
                	objetoOut.writeObject(mensagemRecebida);
                	
                }
                
                
                              
                
				
				//A MINHA THREAD NO SERVIDOR JA COMPRIU SEU PAPEL E A FINALIZA
				no.close();
				
				
				
				
				
			} catch (IOException| ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
			
			
			
		}
}




	


}
