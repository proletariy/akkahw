package akkaBase;

import java.util.ArrayList;
import java.util.List;

// пакет akka.actor.typed содержит классы ядра akka (это база)
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akkaBase.HelloWorld.ChangeMessage;
import akkaBase.NumberGenerator.Command;
import akkaBase.NumberGenerator.NumGenCommand;

/*
	Создаем класс StatCalcuclator, объекты которого обладают способностью
	а) принимать поток чисел
	б) выводить информацию о полученном потоке чисел(дату и время - среднее значение, кол-во чисел)
	
	Класс наследуется от AbstractBehavior<T>, который типизирован StatCalcuclator.Command 

    Каждый субъект определяет тип T для сообщений, которые он может получать. 
    Сообщения являются неизменяемыми, поддерживают сопоставление с образцом.
    
    При определении актеров и их сообщений придерживаемся рекомендаций:
    1. Поскольку сообщения являются общедоступным API-интерфейсом Actor, 
       рекомендуется качественно именовать сообщения (чтобы смысл их был ясен),
       даже если они просто "переносят тип данных". 
       Это облегчит использование, понимание и отладку.

    2. Сообщения должны быть неизменными, так как они разделены между различными потоками.

    3. Хорошей практикой является размещение связанных с актером сообщений в виде статических классов 
       в классе AbstractBehaavior. Это облегчает понимание того, какие сообщения ожидает 
       и обрабатывает субъект.

    4. Хорошей практикой является получение исходного поведения актера с помощью статического метода фабрики.
*/

public class StatCalculator extends AbstractBehavior<StatCalculator.Command> {

	private long dt = 2000;
	private double avg;
	private int size;
	private long countEnd = 10;
	private List<Integer> numArray= new ArrayList<Integer>();
	
	/* *************** Определяем команды (начало) *************************** 
	   Интерфейс                                                                                   */
	interface Command{}
	
	//...........   Реализация интерфейса command ..............
	public enum StatCalcCommand implements Command{
		HELLO,
		START_CALC
	}
	
	public static class CalcStat implements Command {
		public final List<Integer> numArray;

		public CalcStat(List<Integer> numArray) {
			super();
			this.numArray = numArray;
		}
	}
	
	// ***************команды (конец) ***************************  
   
	// Конструктор 
	private StatCalculator(ActorContext<Command> context) {
		super(context);
	}
	
	// static фабричный метод, 
	public static Behavior<Command> create(){
		return Behaviors.setup(context -> new StatCalculator(context));
	}
	
		@Override
	public Receive<Command> createReceive(){
		return newReceiveBuilder()
			   .onMessageEquals(StatCalcCommand.HELLO,this::onSayHello)
			   .onMessageEquals(StatCalcCommand.START_CALC,this::onStartCalc)
			   .onMessage(CalcStat.class, this::onCalcStat)
			   .build();
	}
	
	// Обработчики событий изменения сообщения
	private Behavior<Command> onCalcStat(CalcStat command){
		getContext().getLog().info("{}", command.numArray);
		numArray = command.numArray;
		return this;
	}
	// Обработчики событий передачи сообщения
	private Behavior<Command> onSayHello(){
		getContext().getLog().info("Hello, I am StatCalculator");
		numArray.add(-5);
		return this;
	}
	
	private Behavior<Command> onStartCalc(){
		//while (countEnd-- > 0) {
			/*try {
				Thread.sleep(dt);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			avg=0;
			numArray.forEach(el -> {
				avg += el;
			});
			size = numArray.size();
			avg/=size;
			getContext().getLog().info("avg = {} , size = {} ", avg, size);
			
		//}
		return this;
	}
	
	
		
}
