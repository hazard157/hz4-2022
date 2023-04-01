package com.hazard157.lib.core.glib.cellsgrid;

import org.toxsoft.core.tslib.bricks.geometry.ITsRectangle;
import org.toxsoft.core.tslib.coll.IList;
import org.toxsoft.core.tslib.utils.errors.TsIllegalArgumentRtException;

/**
 * Интерфейс только-для-чтения" к сетке ячеек {@link CellsGrid}.
 * <p>
 * Выста канвы {@link #getCanvasHeight()} вычисляется в зависимости от параметров {@link #margins()}, размеров ячейки
 * {@link #getCellHeight()} и {@link #getCellWidth()}, количества ячеек {@link #getCellsCount()} для заданной ширины
 * {@link #getCanvasWidth()}. Высота канвы определяется как минимальная высота, в которую влезают все строки ячеек с
 * заданнымы параметрами и интервалами. При изменении ширины канвы меняется подбирается количество столцов так, чтобы
 * все ячейки в строке вместились в ширину. Только если ширина меньше одного столюца, единственный столбец будет обрезан
 * справа.
 *
 * @author hazard157
 */
public interface ICellsGrid {

  /**
   * Возвращает параметры настроки границ и интервалов сетки.
   *
   * @return {@link IGridMargins} - параметры настроки границ и интервалов сетки
   */
  IGridMargins margins();

  /**
   * Возвращает координаты всех ячеек.
   *
   * @return {@link IList}&lt;{@link ITsRectangle}&gt; - список (возможно пустой) координат всех ячеек
   */
  IList<ITsRectangle> getCells();

  /**
   * Возвращает заданную пользователем ширину канвы.
   *
   * @return int - ширина канвы в пикселях
   */
  int getCanvasWidth();

  /**
   * Возвращает вычисленную высоту канвы.
   *
   * @return int - высота канвы в пикселях
   */
  int getCanvasHeight();

  /**
   * Возвращает количество строк, которое зависит от количества ячеек.
   *
   * @return int - количество строк, всегда >= 0
   */
  int getRowsCount();

  /**
   * Возвращает количество столбцов в сетке (вне зависимости от количества ячеек).
   *
   * @return int - количнство столбцов, всегда >= 1
   */
  int getColsCount();

  /**
   * Возвращает ширину ячекйи.
   *
   * @return int - ширина ячейки в пикселях, всегда >= 1
   */
  int getCellWidth();

  /**
   * Возвращает высоту ячекйи.
   *
   * @return int - высота ячейки в пикселях, всегда >= 1
   */
  int getCellHeight();

  /**
   * Возвращает количество ячеек.
   *
   * @return int - количество ячеек, всегда >= 0
   */
  int getCellsCount();

  /**
   * Возвращает количество столбцов (точнее, использованных ячеек) в строке.
   * <p>
   * В последней строке может быть от 1-го до {@link #getColsCount()} ячеек, во всех остальных - ровно
   * {@link #getColsCount()}.
   * <p>
   * Если аргумент выходит за допустимые пределы, метод просто возвращает 0.
   *
   * @param aRow int - индекс столбца
   * @return int - количество использованных ячеек в строке
   */
  int getColsInRow( int aRow );

  /**
   * Возвращает Y-координату левого верхнего угла прямоугольника заданной строки.
   * <p>
   * Первая (точнее, нулевая по индексу) строка начинается с координаты y = {@link IGridMargins#topMargin()}. Каждая
   * следующая строка расположена ниже на высоту строки. Высота строки составляет высоту ячейки {@link #getCellHeight()}
   * плюс межстрочное расстояние {@link IGridMargins#verInterval()}.
   * <p>
   * Аргмуент может принимать любое значение, как положительное, так и отрицательное.
   *
   * @param aRow int - индекс строки
   * @return int - Y-координата
   */
  int getRowY( int aRow );

  /**
   * Возвращает X-координату левого верхнего угла прямоугольника заданноого столбца.
   * <p>
   * Первый (точнее, нулевой по индексу) стоолюец начинается с координаты x = {@link IGridMargins#leftMargin()}. Каждый
   * следующий столюец расположен правее на ширину столбца. Ширина столюца составляет ширину ячейки
   * {@link #getCellWidth()} плюс расстояние {@link IGridMargins#horInterval()}.
   * <p>
   * Аргмуент может принимать любое значение, как положительное, так и отрицательное.
   *
   * @param aCol int - индекс столбца
   * @return int - X-координата
   */
  int getColX( int aCol );

  /**
   * Возвращает индекс ячейки, в которой находится точка с заданными координатами.
   * <p>
   * Обратите внимание, что учитываются только прямоугольники ячеек {@link #getCells()}, если точка на в интервале между
   * ячейками или ячейками и границамы канвы, возвращает -1.
   *
   * @param aX int - X-координата точки в пикселях
   * @param aY int - Y-координата точки в пикселях
   * @return int - индекс ячейки или -1
   */
  int getIndexAtCoors( int aX, int aY );

  /**
   * Возвращает индекс ячейки по строке и столбцу.
   * <p>
   * Обратите внимание, что возвращается индекс только существующей ячейке. То есть, если аргументы указывают на
   * остуствующие в последней строке ячейки, метод dthytn -1.
   *
   * @param aCol int - индекс столбца
   * @param aRow int - индекс строки
   * @return int - индекс ячейки или -1
   */
  int getIndex( int aCol, int aRow );

  /**
   * Возвращает размеры ячейки по строке и столбцу.
   * <p>
   * Обратите внимание, что возвращается только существующая ячейка. То есть, если аргументы указывают на остуствующие в
   * последней строке ячейки, метод выбросит исключение.
   *
   * @param aCol int - индекс столбца
   * @param aRow int - индекс строки
   * @return {@link ITsRectangle} - положение и размер ячейки в пикселях
   * @throws TsIllegalArgumentRtException аргументы выходят за допустимые пределы
   */
  ITsRectangle getCell( int aCol, int aRow );

  /**
   * Возвращает размеры ячейки по индексу в списке ячеек.
   *
   * @param aIndex int - индекс в списке ячеек
   * @return {@link ITsRectangle} - положение и размер ячейки в пикселях
   * @throws TsIllegalArgumentRtException индекс выходит за лдопустимые пределы
   */
  ITsRectangle getCell( int aIndex );

}
