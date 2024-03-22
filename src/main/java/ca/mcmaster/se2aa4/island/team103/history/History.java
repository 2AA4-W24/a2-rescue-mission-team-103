package ca.mcmaster.se2aa4.island.team103.history;
import java.util.List;

public interface History<T> {
	public void addItem(T item);
	public T getLast();
	public List<T> getItems(int offset);
	public List<T> getItems(int start, int end);
	public int getSize();
}
