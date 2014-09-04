package com.lucas.shakepicture;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;

/**
 * 一个左右摇摆的数据结构
 * @author Lucas
 *
 * @param <E> The element type of this list.
 */
public class SwingList<E> extends ArrayList<E> {

    public interface OnOnePeriodListener {
        public void onOnePeriod(); // 震动一个周期的回调
    }
    
    private OnOnePeriodListener listener;
    
    public SwingList(OnOnePeriodListener onOnePeriodListener) {
        this.listener = onOnePeriodListener;
    }
    
    @Override
    public SwingListIterator<E> iterator() {
        return new SwingListIteratorImpl();
    }
    
//    public SwingListIterator<E> iterator(int beginIndex) {
//        if(beginIndex < 0 || beginIndex > size() - 1) {
//            throw new IndexOutOfBoundsException();
//        }
//        
//        return new SwingListIteratorImpl(beginIndex);
//    }
    
 //   private int tmp = 0;
    
    private class Index {
        private boolean increase = true;        
        private int i = 0;
        
 //       public Index() {}
        
//        public Index(int beginIndex) {
//            if(beginIndex < 0 || beginIndex > size() - 1) {
//                throw new IndexOutOfBoundsException();
//            }
//            
//            i = beginIndex;
//        }

        public int next() {
            if (i == size() - 1)
                increase = false;

            if (i == 0 && !increase) {
                if(listener != null) {
                    // 减小到了0，说明过了一个周期了
                    listener.onOnePeriod();
                }
                
                increase = true;
            }

            int t = i;

            if (increase)
                ++i;
            else
                --i;

            return t;
        }
        
//        public int next(int step) {
//            for(int i = 1; i < step; i++) {
//                next();
//            }
//            
//            return next();
//        }
    }
    
    public interface SwingListIterator<E> extends Iterator<E> {
   //     public E next(int step);
    }
    
    private class SwingListIteratorImpl implements SwingListIterator<E> {

        private Index index;
        
        public SwingListIteratorImpl() {
            index = new Index();
        }
        
//        public SwingListIteratorImpl(int beginIndex) {
//            index = new Index(beginIndex);
//        }
        
        @Override
        public boolean hasNext() {
            if(SwingList.this.isEmpty()) 
                return false;
            
            // 因为是左右摇摆的list，所以总有next
            return true;
        }
        
        @Override
        public E next() {
            return SwingList.this.get(index.next());
        }
        
//        @Override
//        public E next(int step) {
//            return SwingList.this.get(index.next(step));
//        }
        
        @Override
        public void remove() {            
            // 此方法暂时未实现
            throw new UnsupportedOperationException();
        }
    }

}

