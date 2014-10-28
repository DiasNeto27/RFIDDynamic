package br.edu.fatecbauru.controllers;

public class Campo {
   private String nome;
   private Object valor;
   private Integer bloco;
   
   public void setNome(String nome){
	   this.nome = nome;
   }
   
   public void setValor(Object valor){
	   this.valor = valor;
   }
   
   public void setBloco(Integer bloco){
	   this.bloco = bloco;
   }
   
   public String getNome(){
	   return this.nome;
   }
   
   public Object getValor(){
	   return this.valor;
   }
   
   public Integer getBloco(){
	   return this.bloco;
   }
   
}
