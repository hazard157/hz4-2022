package com.hazard157.lib.core.excl_done.cellsgrid;

/**
 * Параметры настройка границ и интервалов рисования сетки значков.
 * <p>
 * Реализован как паттерн "интерфейс только-для-чтения и редактируемый класс".
 *
 * @author hazard157
 */
public interface IGridMargins {

  /**
   * Возвращает толщину границы вокруг ячейки сетки.
   *
   * @return - int толщина границы вокруг ячейки сетки в пикселях
   */
  int borderWidth();

  /**
   * Возвращает расстояние между сеткой и левым краем панели.
   *
   * @return int - расстояние между сеткой и левым краем панели в пикселях
   */
  int leftMargin();

  /**
   * Возвращает расстояние между сеткой и правым краем панели.
   *
   * @return int - расстояние между сеткой и правым краем панели в пикселях
   */
  int rightMargin();

  /**
   * Возвращает расстояние между сеткой и верхним краем панели.
   *
   * @return int - расстояние между сеткой и верхним краем панели в пикселях
   */
  int topMargin();

  /**
   * Возвращает расстояние между сеткой и нижним краем панели.
   *
   * @return int - расстояние между сеткой и нижним краем панели в пикселях
   */
  int bottomMargin();

  /**
   * Возвращает расстояние между ячейками сетки по горизонтали.
   *
   * @return int - расстояние между ячейками сетки по горизонтали в пикселях
   */
  int horInterval();

  /**
   * Возвращает расстояние между ячейками сетки по вертикали.
   *
   * @return int - расстояние между ячейками сетки по вертикали в пикселях
   */
  int verInterval();

}
