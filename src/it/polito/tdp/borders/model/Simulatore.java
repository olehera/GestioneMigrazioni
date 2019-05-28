package it.polito.tdp.borders.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

public class Simulatore {
	
// 	Modello --> Stato del sistema ad ogni passo
	private Graph<Country, DefaultEdge> grafo;
 
//  Tipi di evento / coda prioritaria   
	private PriorityQueue<Evento> queue;
 
//  Parametri della simulazione
	private int N_MIGRANTI = 1000;
	
//	Valori di output
	private int T;
	private Map<Country, Integer> stanziali;
	
	public void init(Country partenza, Graph<Country, DefaultEdge> grafo) {
//		Ricevo i parametri
		this.grafo = grafo;

//		Impostazione dello stato iniziale
		this.T = 1;
		stanziali = new HashMap<Country, Integer>();
		
		for (Country c : this.grafo.vertexSet() )
			stanziali.put(c, 0);
		
		queue = new PriorityQueue<Evento>();
		
//		Inserisco il primo evento
		this.queue.add(new Evento(T, N_MIGRANTI, partenza));
	}

	public void run() {
//		Estraggo un evento per volta dalla coda e lo eseguo, finchè la coda non si svuota
		Evento evento;
		
		while ( (evento = queue.poll()) != null ) {
//			Eseguo l'evento
			this.T = evento.getT();
			int nPersone = evento.getN();
			Country stato = evento.getStato();
			List<Country> confinanti = Graphs.neighborListOf(this.grafo, stato);
			int migranti = (nPersone/2)/confinanti.size();
			
			if (migranti > 0) {
//				Le persone si possono muovere
				for (Country confinante : confinanti)
					queue.add(new Evento(T+1, migranti, confinante));
			}
			
			int stanziali = nPersone - migranti*confinanti.size();
			this.stanziali.put(stato, this.stanziali.get(stato) + stanziali);
		}
	}

	public int getLastT() {
		return T;
	}

	public Map<Country, Integer> getStanziali() {
		return stanziali;
	}
}
