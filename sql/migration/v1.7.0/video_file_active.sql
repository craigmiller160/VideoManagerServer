-- Already run in dev & prod
ALTER TABLE video_files
ADD COLUMN active BOOLEAN DEFAULT true;
