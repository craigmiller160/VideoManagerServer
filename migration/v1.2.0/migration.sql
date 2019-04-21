ALTER TABLE public.video_files
ADD COLUMN last_viewed TIMESTAMP;

UPDATE public.video_files
SET view_count = 0;