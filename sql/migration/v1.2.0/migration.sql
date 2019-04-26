ALTER TABLE public.video_files
ADD COLUMN last_viewed TIMESTAMP;

ALTER TABLE public.video_files
ADD COLUMN file_added TIMESTAMP;

UPDATE public.video_files
SET view_count = 0;