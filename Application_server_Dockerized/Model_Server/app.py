from fastapi import FastAPI, UploadFile, File
from fastapi.responses import FileResponse
import uuid
import os
from model_focus import load_dino_model, preprocess_image, get_attention_map, overlay_heatmap

app = FastAPI()

# Load model + processor once
model, processor = load_dino_model()

OUTPUT_DIR = "outputs"
os.makedirs(OUTPUT_DIR, exist_ok=True)

@app.post("/analyze")
async def analyze_image(file: UploadFile = File(...)):
    temp_path = f"{OUTPUT_DIR}/{uuid.uuid4().hex}_{file.filename}"
    with open(temp_path, "wb") as f:
        f.write(await file.read())

    img_tensor, pil_image = preprocess_image(temp_path, processor)
    heatmap = get_attention_map(model, img_tensor)
    result_path = os.path.join(OUTPUT_DIR, f"focus_{os.path.basename(temp_path)}")
    overlay_heatmap(pil_image, heatmap, result_path)

    return FileResponse(result_path, media_type="image/jpeg")
