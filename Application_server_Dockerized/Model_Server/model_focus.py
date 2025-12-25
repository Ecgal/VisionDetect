import torch
from transformers import ViTImageProcessor, ViTModel
import numpy as np
from PIL import Image
import matplotlib.pyplot as plt
from transformers import ViTModel
from huggingface_hub import snapshot_download


def load_dino_model():
    model = ViTModel.from_pretrained("./models/dino-vits8", output_attentions=True)
    processor = ViTImageProcessor.from_pretrained("./models/dino-vits8")
    model.eval()
    return model, processor




def preprocess_image(image_path, processor):
    image = Image.open(image_path).convert("RGB")
    inputs = processor(images=image, return_tensors="pt")
    return inputs, image


def get_attention_map(model, inputs):
    with torch.no_grad():
        outputs = model(**inputs)
        attentions = outputs.attentions
    attn = attentions[-1].mean(1)[0]

    num_tokens = attn.shape[-1]
    grid_size = int(num_tokens ** 0.5)
    attn = attn[1:, 1:].mean(0).reshape(grid_size, grid_size).cpu().numpy()
    attn = attn / attn.max()
    return attn



def overlay_heatmap(image, heatmap, save_path="output_focus.jpg"):
    heatmap = np.uint8(255 * heatmap)
    heatmap = np.array(Image.fromarray(heatmap).resize(image.size, resample=Image.BILINEAR))
    heatmap = plt.cm.jet(heatmap)[:, :, :3]
    overlay = np.array(image) / 255.0 * 0.6 + heatmap * 0.4
    overlay = np.uint8(overlay / overlay.max() * 255)
    out_img = Image.fromarray(overlay)
    out_img.save(save_path)
    return save_path


def analyze_focus(image_path, output_path="output_focus.jpg"):
    model, processor = load_dino_model()
    inputs, pil_image = preprocess_image(image_path, processor)
    heatmap = get_attention_map(model, inputs)
    result_path = overlay_heatmap(pil_image, heatmap, output_path)
    print(f"Saved AI focus visualization at: {result_path}")


if __name__ == "__main__":
    analyze_focus("test.jpg", "focus_result.jpg")
