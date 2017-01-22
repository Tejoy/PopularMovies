package com.nanodegree.tejomai.popularmovies;

public class AsyncTaskItem<T> {
        private T result;
        private Exception error;

        public T getResult() {
            return result;
        }

        public Exception getError() {
            return error;
        }

        public AsyncTaskItem(T result) {
            super();
            this.result = result;
        }

        public AsyncTaskItem(Exception error) {
            super();
            this.error = error;
        }
}
