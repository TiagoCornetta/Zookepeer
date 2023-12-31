import java.io.Serializable;

public class Mensagem implements Serializable {
	
	public String acao;
	public String key = null;
	public String value = null;
	public int timeStamp = 0;
	public String ipServidor = null; 
	public int portaServ = 0;
	public String m = null;
	public String ipCliente = null;
	public int portaCliente = 0;
	
	
	public Mensagem(String acao,String key,String value,int timeStamp, String ipServidor, int portaServ, String m,String ipCliente, int portaCliente) {
		this.acao = acao;
		this.key = key;
		this.value = value;
		this.timeStamp = timeStamp;
		this.ipServidor = ipServidor;
		this.portaServ = portaServ;
		this.m = m;
		this.ipCliente = ipCliente;
		this.portaCliente = portaCliente;
	}

	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(int timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getIpServidor() {
		return ipServidor;
	}

	public void setIpServidor(String ipServidor) {
		this.ipServidor = ipServidor;
	}

	public int getPortaServ() {
		return portaServ;
	}

	public void setPortaServ(int portaServ) {
		this.portaServ = portaServ;
	}

	public String getM() {
		return m;
	}

	public void setM(String m) {
		this.m = m;
	}

	public String getIpCliente() {
		return ipCliente;
	}

	public void setIpCliente(String ipCliente) {
		this.ipCliente = ipCliente;
	}

	public int getPortaCliente() {
		return portaCliente;
	}

	public void setPortaCliente(int portaCliente) {
		this.portaCliente = portaCliente;
	}

	

	


}


	
