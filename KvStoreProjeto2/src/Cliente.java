

import java.net.Socket;
import java.util.Scanner;
import java.util.ArrayList; 
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.Random;

public class Cliente {
	
	public static void main(String[] args) throws Exception {	
		
				//scan para pegar o que o usuário necessita informar
				Scanner scan = new Scanner(System.in);
				//random para conectar em um servidor aleatorio
				Random gerador = new Random();
				
				//variavel que ira pegar o IP dos servidores e porta deles
				String auxIp;
				int porta;
				
				//variaveis que armazenam informações do cliente
				String ipCliente;
				int portaCliente;
				
				//TimeStamp do meu cliente inicializado em zero
				int timeStamp  = 0;
				
				//variavel que ira pegar o key e o value
				String key = null, value = null;
				
				//lista de IPS dos servidores 
		        ArrayList<Integer> portas = new ArrayList<>();
		        
		        
				//menu para INIT
		        int init = 0;
		        System.out.println("(MENU DE INIT DO CLIENTE)");
		        while(init!=1) {
		        	System.out.println("DIGITE 1 PARA INICAR O CLIENTE:");
		        	init = scan.nextInt(); scan.nextLine();
		        }
		        System.out.println();
				
		        
		        
		        //menu para pegar as informações do cliente
		        System.out.println("(INFORMACOES CLIENTE)");
		        System.out.println("DIGITE O IP DO CLIENTE:");
		        ipCliente = scan.nextLine();
		        System.out.println("DIGITE A PORTA DO CLIENTE:");
		        portaCliente = scan.nextInt(); scan.nextLine();
		        System.out.println();
		        
		        
		        
				
				//Para pegar o IP e Porta dos 3 servidores
				//Como estamos trabalhando em uma mesma máquina os servidores se diferencirão por portas e não por ip
		        System.out.println("(INFORMACOES SERVIDORES)");
				for(int i = 0; i < 3 ; i++) {
					int aux = i + 1;
					System.out.println("DIGITE O IP DO SERVIDOR [" + aux  + "]:");
					auxIp = scan.nextLine();
					System.out.println("DIGITE A PORTA DO SERVIDOR [" + aux + "]:");
					porta = scan.nextInt(); scan.nextLine();
					portas.add(porta);
				}
				
			
				
				//Menu de interação com o cliente
				int n = 0;
				while(n !=3) {
					System.out.println("(MENU PRINCIPAL)");
					System.out.println("DIGITE A OPCAO QUE DESEJA REALIZAR:");
					System.out.println("1 - PUT ");
					System.out.println("2 - GET");
					System.out.println("3 - FINALIZAR");
					n = scan.nextInt();scan.nextLine();
					
					
	                // PUT DO CLIENTE
					if(n == 1) {
						//Pegando a key e o value a serem enviados ao servidor
						System.out.println();
						System.out.println("(MENU PUT)");
						System.out.println("DIGITE A KEY A SER ENVIADA:");
						key = scan.nextLine();
						System.out.println("DIGITE O VALUE A SER ENVIADO:");
						value = scan.nextLine();
						
						//Cria uma conexão com o servidor
						Socket s = new Socket("127.0.0.1",portas.get(gerador.nextInt(3)));
						
						//Ira definir os objetos de saida e entrada a serem enviados por tcp
						ObjectOutputStream objetoOut = new ObjectOutputStream(s.getOutputStream());
		                ObjectInputStream objetoIn = new ObjectInputStream(s.getInputStream());						
						
		                //Cria a mensagem a ser enviada
		                Mensagem mensagem = new Mensagem("PUT",key,value,timeStamp,null,0,null,ipCliente,portaCliente);
					
						//Classe a ser enviada
						objetoOut.writeObject(mensagem);
						
						//Classe a ser recebida;
						Mensagem mensagemRecebida = (Mensagem) objetoIn.readObject();
						
						timeStamp = mensagemRecebida.getTimeStamp();
						
						//Mensagem de put recebida
						System.out.println();
						System.out.println("PUT_OK key: " + mensagemRecebida.getKey() + " value: " + mensagemRecebida.getValue() + " timestamp: " + mensagemRecebida.getTimeStamp());
						System.out.println("Realizada no servidor IP: " + mensagemRecebida.getIpServidor() + " Porta: "+ mensagemRecebida.getPortaServ());
						System.out.println();
						
						//Fechamento do canal
						s.close();
						
												
						
						
					}
					//GET DO CLIENTE
					else if (n == 2) {
						System.out.println();
						System.out.println("MENU GET");
						System.out.println("DIGITE A KEY A SER PROCURADA:");
						key= scan.nextLine();
						
						
						
						//Cria uma conexão com o servidor
						Socket s = new Socket("127.0.0.1",portas.get(gerador.nextInt(3)));
						
						//Ira definir os objetos de saida e entrada a serem enviados por tcp
						ObjectOutputStream objetoOut = new ObjectOutputStream(s.getOutputStream());
		                ObjectInputStream objetoIn = new ObjectInputStream(s.getInputStream());						
						
		                //Cria a mensagem a ser enviada
		                Mensagem mensagem = new Mensagem("GET",key,value,timeStamp,null,0,null,ipCliente,portaCliente);
					
						//Classe a ser enviada
						objetoOut.writeObject(mensagem);
						
						//Classe a ser recebida;
						Mensagem mensagemRecebida = (Mensagem) objetoIn.readObject();
						
						System.out.println("GET key: " + mensagemRecebida.getKey() + " value: " + mensagemRecebida.getValue() + " obtido servidor IP: "+ mensagemRecebida.getIpServidor()+ ":" + mensagemRecebida.getPortaServ()) ;
						System.out.println(",meu timestamp: "+ timeStamp +  " do servidor: " + mensagemRecebida.getTimeStamp());
						
						
						
						//Fechamento do canal
						s.close();
					}
					else {
						System.out.println("Muito obrigado, volte sempre! <3");
					}
					
				}
				
				
				scan.close();
				
				
				
	}
}
